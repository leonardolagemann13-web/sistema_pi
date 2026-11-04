package util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import model.Pagamentos;
import model.Relatorio;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class RelatorioPDF {
    // Definindo os formatadores de data, hora e linguagem
    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Definição das cores usadas
    private static final Color VERDE = new Color(52,94,56);
    private static final Color VERDE_CLARO = new Color(222,236,220);
    private static final Color CINZA_FUNDO = new Color(242,242,242);
    private static final Color CINZA_TEXTO = new Color(100,100,100);
    private static final Color BORDA = new Color(200,200,200);

    private static Font fonte(float tamanho, int estilo, Color cor) {
        return new Font(Font.HELVETICA, tamanho, estilo, cor);
    }

    private static PdfPCell celula(String texto, Font fonte, int alinhamento, Color fundo) {
        PdfPCell celula = new PdfPCell(new Phrase(texto == null ? "" : texto, fonte));
        celula.setHorizontalAlignment(alinhamento);
        celula.setPadding(7);
        celula.setBorderColor(BORDA);
        if (fundo != null){
            celula.setBackgroundColor(fundo);
        }
        return celula;
    }

    private static void linhaResumo(PdfPTable tabela, String rotulo, String valor, boolean destaque) {
        tabela.addCell(celula(rotulo, fonte(11, Font.BOLD, Color.BLACK), Element.ALIGN_LEFT, CINZA_FUNDO));
        tabela.addCell(celula(valor, fonte(11, destaque ? Font.BOLD : Font.NORMAL, Color.BLACK),
                Element.ALIGN_RIGHT, destaque ? VERDE_CLARO : null));
    }

    // Monta o texto do intervalo
    private static String formatarIntervalo(Relatorio r) {
        if (r.getDataInicio() == null || r.getDataFim() == null){
            return "";
        }
        return r.getDataInicio().format(FORMATO_DATA) + " a " + r.getDataFim().format(FORMATO_DATA);
    }

    // Formata como moeda brasileira (R$ 1.234,50)
    private static String moeda(double valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor)
                .replace('\u00A0', ' ')
                .replace('\u202F', ' ');
    }

    public static void gerar(File destino, Relatorio relatorio, List<Pagamentos> pagamentos) throws DocumentException, IOException {
        FileOutputStream saida = new FileOutputStream(destino);
        // Cria o documento
        Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
        try {
            PdfWriter.getInstance(doc, saida);
            doc.open();

            Paragraph titulo = new Paragraph("Relatório Financeiro", fonte(20, Font.BOLD, VERDE));
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Sistema de Venda de Produtos Agrícolas", fonte(11, Font.NORMAL, CINZA_TEXTO));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitulo);

            Paragraph emissao = new Paragraph("Emitido em " + LocalDateTime.now().format(FORMATO_DATA_HORA), fonte(9, Font.NORMAL, CINZA_TEXTO));
            emissao.setAlignment(Element.ALIGN_CENTER);
            emissao.setSpacingAfter(20);
            doc.add(emissao);

            doc.add(tabelaResumo(relatorio));

            Paragraph secao = new Paragraph("Pagamentos do período", fonte(13, Font.BOLD, VERDE));
            secao.setSpacingBefore(25);
            secao.setSpacingAfter(8);
            doc.add(secao);

            if (pagamentos == null || pagamentos.isEmpty()) {
                doc.add(new Paragraph("Nenhum pagamento registrado neste período.", fonte(10, Font.NORMAL, Color.BLACK)));
            } else {
                doc.add(tabelaPagamento(pagamentos));
            }
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
            saida.close();
        }
    }

        // Tabela com o resumo financeiro
        private static PdfPTable tabelaResumo(Relatorio r) throws DocumentException {
            PdfPTable tabela = new PdfPTable(2);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new float[]{35f, 65f});

            linhaResumo(tabela, "Período", r.getPeriodo(), false);
            linhaResumo(tabela, "Intervalo", formatarIntervalo(r), false);
            linhaResumo(tabela, "Valor arrecadado", moeda(r.getValorArrecadado()), false);
            linhaResumo(tabela, "Despesas", moeda(r.getDespesas()), false);
            linhaResumo(tabela, "Valor total arrecadado", moeda(r.getValorTotalArrecadado()), true);
            return tabela;
        }

        // Tabela com o detalhamento dos pagamentos
        private static PdfPTable tabelaPagamento(List<Pagamentos> pagamentos) throws DocumentException {
            PdfPTable tabela = new PdfPTable(5);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new float[]{16f, 34f, 14f, 16f, 20f});
            tabela.setHeaderRows(1);

            String[] cabecalho = {"Data", "Cliente", "Encomenda", "Forma", "Valor"};
            for (int i = 0; i < cabecalho.length; i++){
                tabela.addCell(celula(cabecalho[i], fonte(10, Font.BOLD, Color.WHITE),
                        i == 4 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT, VERDE));
            }

            double total = 0;
            Font corpo = fonte(10, Font.NORMAL, Color.BLACK);
            for (Pagamentos p : pagamentos){
                LocalDate data = p.getDataPagamento();
                String cliente = p.getIdEncomenda().getIdCliente().getNome() + " " + p.getIdEncomenda().getIdCliente().getSobrenome();

                tabela.addCell(celula(data == null ? "" : data.format(FORMATO_DATA), corpo, Element.ALIGN_LEFT, null));
                tabela.addCell(celula(cliente, corpo, Element.ALIGN_LEFT, null));
                tabela.addCell(celula("#" + p.getIdEncomenda().getIdEncomenda(), corpo, Element.ALIGN_LEFT, null));
                tabela.addCell(celula(p.getFormaPagamento() == null ? "" : p.getFormaPagamento(), corpo, Element.ALIGN_LEFT, null));
                tabela.addCell(celula(moeda(p.getValorTotal()), corpo, Element.ALIGN_RIGHT, null));
                total += p.getValorTotal();
            }

            // Linha de total
            Font negrito = fonte(10, Font.BOLD, Color.BLACK);
            PdfPCell rotuloTotal = celula("Total do período", negrito, Element.ALIGN_RIGHT, CINZA_FUNDO);
            rotuloTotal.setColspan(4);
            tabela.addCell(rotuloTotal);
            tabela.addCell(celula(moeda(total), negrito, Element.ALIGN_RIGHT, CINZA_FUNDO));
            return tabela;
        }
    }