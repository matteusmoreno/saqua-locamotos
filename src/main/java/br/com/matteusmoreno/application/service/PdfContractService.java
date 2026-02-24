package br.com.matteusmoreno.application.service;

import br.com.matteusmoreno.domain.constant.MaritalStatus;
import br.com.matteusmoreno.domain.constant.RentalType;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.model.Address;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
@Slf4j
public class PdfContractService {

    // -----------------------------------------------------------------------
    // Cores e fontes
    // -----------------------------------------------------------------------
    private static final Color COLOR_DARK    = new Color(25, 25, 25);
    private static final Color COLOR_PRIMARY = new Color(30, 64, 120);
    private static final Color COLOR_LIGHT   = new Color(245, 246, 250);
    private static final Color COLOR_BORDER  = new Color(200, 210, 225);

    private static final Font FONT_TITLE   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  16, COLOR_PRIMARY);
    private static final Font FONT_SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  10, Color.WHITE);
    private static final Font FONT_LABEL   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9, COLOR_DARK);
    private static final Font FONT_VALUE   = FontFactory.getFont(FontFactory.HELVETICA,         9, COLOR_DARK);
    private static final Font FONT_CLAUSE  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9, COLOR_PRIMARY);
    private static final Font FONT_BODY    = FontFactory.getFont(FontFactory.HELVETICA,         8, COLOR_DARK);
    private static final Font FONT_SIGN    = FontFactory.getFont(FontFactory.HELVETICA,         9, COLOR_DARK);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
            new java.util.Locale("pt", "BR"));

    // -----------------------------------------------------------------------
    // Ponto de entrada
    // -----------------------------------------------------------------------
    public byte[] generateContractPdf(Contract contract) {
        log.info("Generating PDF for contract: {}", contract.getContractId());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 50, 50, 60, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new HeaderFooterEvent(contract.getContractId()));
            doc.open();

            User    tenant      = contract.getUser();
            Motorcycle moto     = contract.getMotorcycle();

            // Dados fixos do locador (empresa)
            String locadorNome      = "Matteus Guimarães Moreno";
            String locadorNacional  = "brasileiro";
            String locadorEstado    = "divorciado";
            String locadorOcupacao  = "empresário";
            String locadorRg        = "256854282";
            String locadorCpf       = "140.676.027-74";
            String locadorEndereco  = "Rua Moacir Picanço, 21. Bacaxá, Saquarema - RJ. CEP: 28994-666";
            String locadorEmail     = "matteus_moreno@live.com";
            String locadorTelefone  = "(22)99822-3307";

            // ---- Cabeçalho -----------------------------------------------
            addHeader(doc);

            // ---- Título -------------------------------------------------
            Paragraph title = new Paragraph("CONTRATO DE LOCAÇÃO DE VEÍCULO PARTICULAR", FONT_TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(4);
            doc.add(title);

            Paragraph subtitle = new Paragraph(rentalTypeLabel(contract.getRentalType()), FONT_CLAUSE);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            doc.add(subtitle);

            // ---- Partes -----------------------------------------------------
            addSectionHeader(doc, "QUALIFICAÇÃO DAS PARTES");

            addPartyTable(doc,
                    "LOCADOR",
                    locadorNome, locadorNacional, locadorEstado, locadorOcupacao,
                    locadorRg, locadorCpf, locadorEndereco, locadorEmail, locadorTelefone);

            addPartyTable(doc,
                    "LOCATÁRIO",
                    tenant.getName(),
                    "brasileiro",
                    maritalStatusPt(tenant.getMaritalStatus()),
                    tenant.getOccupation() != null ? tenant.getOccupation() : "—",
                    tenant.getRg(),
                    tenant.getCpf(),
                    formatAddress(tenant.getAddress()),
                    tenant.getEmail() != null ? tenant.getEmail() : "Não informado",
                    tenant.getPhone());

            // ---- Veículo ----------------------------------------------------
            addSectionHeader(doc, "CLÁUSULA 1ª – DO OBJETO DO CONTRATO");
            addVehicleTable(doc, moto);
            addClauseText(doc, "1.2.", "O veículo descrito acima será utilizado exclusivamente pelo LOCATÁRIO, não sendo " +
                    "permitido sub-rogar para terceiros os direitos por ele obtidos através do presente contrato, nem " +
                    "permitir que outra pessoa conduza o referido veículo sem a inequívoca e expressa autorização do " +
                    "LOCADOR, sob pena de rescisão contratual, multa de R$ 400,00 (quatrocentos reais) bem como " +
                    "responsabilidade total por qualquer ato ou dano em relação ao veículo, inclusive os provenientes " +
                    "de caso fortuito ou força maior.");

            // ---- Cláusulas --------------------------------------------------
            addClausesBlock(doc, contract);

            // ---- Assinaturas -----------------------------------------------
            addSignatureBlock(doc, contract);

            doc.close();
            log.info("PDF generated successfully for contract: {}", contract.getContractId());
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF for contract {}: {}", contract.getContractId(), e.getMessage());
            throw new RuntimeException("Failed to generate contract PDF", e);
        }
    }

    // -----------------------------------------------------------------------
    // Cabeçalho da empresa
    // -----------------------------------------------------------------------
    private void addHeader(Document doc) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_PRIMARY);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(12);

        Paragraph p = new Paragraph("SAQUA LOCAMOTOS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);

        Paragraph sub = new Paragraph("Locação de Motocicletas • Saquarema – RJ", FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(200, 220, 255)));
        sub.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(sub);

        table.addCell(cell);
        table.setSpacingAfter(14);
        doc.add(table);
    }

    // -----------------------------------------------------------------------
    // Faixa de seção
    // -----------------------------------------------------------------------
    private void addSectionHeader(Document doc, String text) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(12);
        table.setSpacingAfter(6);

        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_SECTION));
        cell.setBackgroundColor(COLOR_PRIMARY);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(6);
        cell.setPaddingLeft(10);
        table.addCell(cell);
        doc.add(table);
    }

    // -----------------------------------------------------------------------
    // Tabela de parte (locador / locatário)
    // -----------------------------------------------------------------------
    private void addPartyTable(Document doc, String role,
                                String nome, String nacional, String estadoCivil,
                                String ocupacao, String rg, String cpf,
                                String endereco, String email, String telefone) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{2f, 5f});
        table.setWidthPercentage(100);
        table.setSpacingAfter(8);

        // Cabeçalho da linha de role
        PdfPCell roleCell = new PdfPCell(new Phrase(role, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_PRIMARY)));
        roleCell.setColspan(2);
        roleCell.setBackgroundColor(COLOR_LIGHT);
        roleCell.setBorderColor(COLOR_BORDER);
        roleCell.setPadding(5);
        table.addCell(roleCell);

        addRow(table, "Nome",          nome);
        addRow(table, "Nacionalidade", nacional);
        addRow(table, "Estado civil",  estadoCivil);
        addRow(table, "Ocupação",      ocupacao);
        addRow(table, "RG",            rg);
        addRow(table, "CPF",           cpf);
        addRow(table, "Endereço",      endereco);
        addRow(table, "E-mail",        email);
        addRow(table, "Telefone",      telefone);

        doc.add(table);
    }

    private void addRow(PdfPTable table, String label, String value) {
        PdfPCell lbl = new PdfPCell(new Phrase(label, FONT_LABEL));
        lbl.setBorderColor(COLOR_BORDER);
        lbl.setBackgroundColor(COLOR_LIGHT);
        lbl.setPadding(4);
        lbl.setPaddingLeft(8);

        PdfPCell val = new PdfPCell(new Phrase(value != null ? value : "—", FONT_VALUE));
        val.setBorderColor(COLOR_BORDER);
        val.setPadding(4);
        val.setPaddingLeft(8);

        table.addCell(lbl);
        table.addCell(val);
    }

    // -----------------------------------------------------------------------
    // Tabela do veículo
    // -----------------------------------------------------------------------
    private void addVehicleTable(Document doc, Motorcycle moto) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk("1.1.  ", FONT_CLAUSE));
        p.add(new Chunk("Por meio deste contrato regula-se a locação do veículo:", FONT_BODY));
        p.setSpacingAfter(6);
        doc.add(p);

        PdfPTable table = new PdfPTable(new float[]{2f, 4f, 2f, 4f});
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);

        addVehicleRow(table, "Marca / Modelo", moto.getBrand() + " / " + moto.getModel(), "Placa",  moto.getPlate());
        addVehicleRow(table, "RENAVAM",        moto.getRenavam(),                           "Chassi", moto.getChassis());
        addVehicleRow(table, "Ano",            moto.getYear(),                              "Cor",    moto.getColor());
        addVehicleRow(table, "Quilometragem",  moto.getMileage() != null ? moto.getMileage() + " km" : "—", "Proprietário", "Matteus Guimarães Moreno");

        doc.add(table);
    }

    private void addVehicleRow(PdfPTable table,
                                String label1, String val1,
                                String label2, String val2) {
        PdfPCell l1 = new PdfPCell(new Phrase(label1, FONT_LABEL));
        l1.setBackgroundColor(COLOR_LIGHT); l1.setBorderColor(COLOR_BORDER); l1.setPadding(4); l1.setPaddingLeft(8);

        PdfPCell v1 = new PdfPCell(new Phrase(val1 != null ? val1 : "—", FONT_VALUE));
        v1.setBorderColor(COLOR_BORDER); v1.setPadding(4); v1.setPaddingLeft(8);

        PdfPCell l2 = new PdfPCell(new Phrase(label2, FONT_LABEL));
        l2.setBackgroundColor(COLOR_LIGHT); l2.setBorderColor(COLOR_BORDER); l2.setPadding(4); l2.setPaddingLeft(8);

        PdfPCell v2 = new PdfPCell(new Phrase(val2 != null ? val2 : "—", FONT_VALUE));
        v2.setBorderColor(COLOR_BORDER); v2.setPadding(4); v2.setPaddingLeft(8);

        table.addCell(l1); table.addCell(v1);
        table.addCell(l2); table.addCell(v2);
    }

    // -----------------------------------------------------------------------
    // Bloco de cláusulas (2 a 10)
    // -----------------------------------------------------------------------
    private void addClausesBlock(Document doc, Contract contract) throws DocumentException {

        // Cláusula 2
        addSectionHeader(doc, "CLÁUSULA 2ª – DO HORÁRIO DO ALUGUEL E LOCAL DE COLETA E DEVOLUÇÃO DO VEÍCULO");
        addClauseText(doc, "2.1.", "O veículo em questão permanecerá na posse do LOCATÁRIO por período integral, de segunda a domingo.");
        addClauseText(doc, "2.2.", "O LOCATÁRIO deverá apresentar o veículo ao LOCADOR 01 (uma) vez por mês para a realização de vistoria, em data e endereço por este designado.");
        addClauseText(doc, "2.3.", "A não apresentação do veículo no prazo e local supracitados acarretará ao LOCATÁRIO multa de R$ 20,00 (vinte reais) por dia de atraso, além de possível rescisão contratual.");

        // Cláusula 3
        addSectionHeader(doc, "CLÁUSULA 3ª – DAS OBRIGAÇÕES DO LOCADOR");
        addClauseText(doc, "3.1.", "O veículo objeto do presente contrato será submetido à manutenção preventiva periódica, ou em decorrência de problemas mecânicos e/ou elétricos aos qual o LOCATÁRIO não deu causa, em oficina mecânica designada pelo LOCADOR, nos termos a seguir:");
        addSubClause(doc,  "3.1.1.", "Troca do Kit de Tração: Sempre que houver barulho anormal e/ou apresentar desgaste excessivo;");
        addSubClause(doc,  "3.1.2.", "Troca de Pneus: Quando estiverem no nível não adequado para uso.");
        addClauseText(doc, "3.2.", "Caso alguma das manutenções supracitadas seja necessária antes ou durante o período estipulado, deverá ser arcada integralmente pelo LOCADOR, salvo nos casos em que o LOCATÁRIO tenha dado causa ao evento, por mau uso.");
        addClauseText(doc, "3.3.", "Os gastos decorrentes da manutenção preventiva periódica supracitada, bem como o valor pago pela mão de obra do profissional que realizará o serviço serão efetuados pelo LOCADOR.");
        addClauseText(doc, "3.4.", "As manutenções que não foram citadas na cláusula 3.1, quando forem necessárias e atestadas pelo mecânico designado, também terão que ser arcadas pelo LOCADOR.");
        addClauseText(doc, "3.5.", "No caso de problemas mecânicos e/ou elétricos percebidos em ocasião diversa da manutenção preventiva periódica, o LOCATÁRIO deverá informar imediatamente ao LOCADOR, bem como disponibilizar o veículo a este, no prazo de 24 horas, para reparo em oficina mecânica designada pelo LOCADOR.");
        addClauseText(doc, "3.6.", "É de responsabilidade do LOCADOR o pagamento do IPVA, Licenciamento e seguro obrigatório do veículo objeto do presente contrato.");
        addClauseText(doc, "3.7.", "O LOCADOR não se obriga a disponibilizar veículo reserva e não se responsabiliza caso o LOCATÁRIO não possa dirigir devido à indisponibilidade do veículo.");

        // Cláusula 4
        addSectionHeader(doc, "CLÁUSULA 4ª – DAS OBRIGAÇÕES DO LOCATÁRIO");
        addClauseText(doc, "4.1.", "É de responsabilidade do LOCATÁRIO a observância básica dos itens do veículo como calibragem dos pneus, nível de óleo do motor, nível de fluido de freio, sistema de iluminação e sinalização, entre outros, bem como o envio de vídeos e fotos a cada 15 dias com o estado de conservação da moto, mostrando a quilometragem, pneus, carenagens etc.");
        addSubClause(doc,  "4.1.1.", "Quaisquer danos/avarias ao veículo serão apurados ao final do contrato e os custos de reparação serão arcados pelo LOCATÁRIO.");
        addSubClause(doc,  "4.1.2.", "Os custos de revisões reparatórias causadas pelo mau uso dos veículos correrão por conta do LOCATÁRIO. Caso a bomba de combustível queime ou danifique por falta de combustível (negligência) o LOCATÁRIO deverá arcar com o valor integral da peça, mão de obra, reboque e demais custos inerentes ao reparo.");
        addClauseText(doc, "4.2.", "É de responsabilidade do LOCATÁRIO o pagamento de quaisquer multas relativas às infrações de trânsito inerentes à utilização do veículo cometidas na vigência deste contrato.");
        addSubClause(doc,  "4.2.1.", "O pagamento das multas pelo LOCATÁRIO deve ser feito imediatamente após a constatação no sistema do DETRAN-RJ, independentemente de qualquer procedimento.");
        addSubClause(doc,  "4.2.2.", "O LOCATÁRIO concorda que o LOCADOR irá indicá-lo como condutor/infrator responsável pelas infrações de trânsito apuradas durante a locação, nos termos do artigo 257 do Código de Trânsito.");
        addSubClause(doc,  "4.2.3.", "Qualquer questionamento sobre eventual improcedência de infração de trânsito deverá ser feito exclusivamente pelo LOCATÁRIO perante o órgão autuador.");
        addSubClause(doc,  "4.2.4.", "Caso o LOCATÁRIO opte por recorrer da autuação e sendo o recurso vitorioso, o LOCADOR lhe fornecerá cópia da guia de pagamento para que ele solicite ao órgão o reembolso.");
        addClauseText(doc, "4.3.", "Ocorrendo multas, o LOCATÁRIO deverá comparecer em local e data estipulada pelo LOCADOR para assinatura do auto de infração, sob pena de pagar R$ 200,00 (duzentos reais) em caso de perda do prazo para a transferência dos pontos.");
        addClauseText(doc, "4.4.", "Caso o veículo seja rebocado por estacionamento irregular, o LOCATÁRIO deverá arcar com todos os custos para recuperação do veículo junto ao depósito público, além de multa contratual de R$ 30,00 (trinta reais) por dia pelo período em que a moto estiver no depósito.");
        addClauseText(doc, "4.5.", "Caso o LOCATÁRIO estacione em local diferente do informado ao LOCADOR, deverá arcar com qualquer dano ou prejuízo pecuniário ao veículo, inclusive inerentes a caso fortuito ou força maior.");
        addClauseText(doc, "4.6.", "É proibido o LOCATÁRIO acionar o serviço de proteção veicular sem a expressa permissão do LOCADOR, sob pena de multa de R$ 200,00 (duzentos reais).");
        addClauseText(doc, "4.7.", "O LOCATÁRIO se responsabiliza por todos os acessórios do veículo que estiverem em sua posse. Caso algum acessório seja perdido ou danificado, o LOCATÁRIO deverá arcar com o custo necessário à reposição.");
        addClauseText(doc, "4.8.", "É proibido o LOCATÁRIO sair do perímetro urbano da Região dos Lagos com o veículo sem a autorização expressa e por escrito do LOCADOR, sob pena de multa de R$ 150,00 (cento e cinquenta reais).");
        addClauseText(doc, "4.9.", "Em caso de roubo ou furto do veículo, o LOCATÁRIO se compromete a avisar imediatamente ao LOCADOR, bem como a comparecer à delegacia de polícia mais próxima para registrar a ocorrência.");
        addClauseText(doc, "4.10.", "O LOCATÁRIO se compromete a comparecer à sede da empresa de Proteção Veicular a fim de cumprir com procedimento de indenização do veículo.");
        addClauseText(doc, "4.11.", "Caso o LOCATÁRIO se envolva em sinistro estando sob efeito de álcool/entorpecentes, deverá pagar ao LOCADOR o valor da tabela FIPE do veículo, caso a indenização seja negada.");
        addClauseText(doc, "4.12.", "O LOCATÁRIO deve manter as características originais do veículo. A instalação de adesivos, pinturas especiais ou acessórios está sujeita à autorização prévia, por escrito, do LOCADOR.");
        addClauseText(doc, "4.13.", "É de responsabilidade do LOCATÁRIO o pagamento e a troca do óleo do motor a cada 1.000km rodados. Serão exigidos foto do painel, vídeo da troca e nota fiscal da compra do óleo.");
        addClauseText(doc, "4.14.", "Aceitar que o LOCADOR promova, pelos meios processuais de que venha a dispor, o seu chamamento aos feitos judiciais promovidos por terceiros decorrentes de eventos com o veículo alugado.");

        // Cláusula 5
        addSectionHeader(doc, "CLÁUSULA 5ª – DAS OBRIGAÇÕES DECORRENTES DE COLISÕES E AVARIAS DO VEÍCULO");
        addClauseText(doc, "5.1.", "É de responsabilidade do LOCATÁRIO o pagamento do reboque, taxas e reparos ao veículo na ocorrência de acidentes e colisões sofridas na vigência do presente contrato quando não contempladas pela cobertura da proteção veicular contratada, independente de dolo, culpa, negligência, imprudência ou imperícia do LOCATÁRIO.");
        addClauseText(doc, "5.2.", "Será de responsabilidade do LOCATÁRIO o pagamento de taxas e diárias para a liberação do veículo decorrentes de reboque realizado pelo poder público.");
        addClauseText(doc, "5.3.", "A responsabilidade determinada nos itens supracitados permanece estabelecida, inclusive, caso o LOCATÁRIO não se encontre no interior do veículo.");

        // Cláusula 6 e 7 — específicas por modalidade
        addPaymentClauses(doc, contract);

        // Cláusula 8
        addSectionHeader(doc, "CLÁUSULA 8ª – DA VIGÊNCIA E RESCISÃO");
        addClauseText(doc, "8.1.", "O presente contrato se inicia na data de sua assinatura com prazo mínimo de 30 dias de locação, após esse prazo a vigência é indeterminada, salvo manifestação de qualquer das partes em contrário.");
        addSubClause(doc,  "8.1.1.", "Em caso de devolução antecipada o LOCATÁRIO pagará uma multa no valor de 30% das diárias canceladas.");
        addClauseText(doc, "8.2.", "É assegurado às partes a resilição do presente contrato a qualquer tempo, bastando dar ciência à outra parte, cabendo ao LOCATÁRIO a devolução do veículo no prazo de 24 horas a contar da comunicação.");
        addClauseText(doc, "8.3.", "O contrato poderá ser considerado rescindido de pleno direito pelo LOCADOR, independentemente de qualquer notificação, quando: (8.3.1) o veículo não for devolvido na data ajustada; (8.3.2) ocorrer uso inadequado; (8.3.3) ocorrer apreensão por autoridades; (8.3.4) o LOCATÁRIO não quitar seus débitos nos vencimentos; (8.3.5) o LOCATÁRIO acumular dívida superior a R$ 100,00 (cem reais) e não a quite imediatamente.");
        addClauseText(doc, "8.4.", "Fica desde já pactuada a total inexistência de vínculo trabalhista entre as partes do presente contrato.");
        addClauseText(doc, "8.5.", "Nos termos do artigo 265 do Código Civil Brasileiro, inexiste solidariedade entre o LOCADOR e o LOCATÁRIO.");

        // Cláusula 9
        addSectionHeader(doc, "CLÁUSULA 9ª – DA DEVOLUÇÃO DO VEÍCULO");
        addClauseText(doc, "9.1.", "Ao término do contrato, o veículo deve ser devolvido em local, dia e hora indicado pelo LOCADOR, sob pena de multa de R$ 50,00 (cinquenta reais) por dia.");
        addClauseText(doc, "9.2.", "A não devolução de veículo pelo LOCATÁRIO, após notificação realizada pelo LOCADOR, configura crime de APROPRIAÇÃO INDÉBITA conforme artigo 168 do Código Penal Brasileiro.");

        // Cláusula 10
        addSectionHeader(doc, "CLÁUSULA 10ª – DAS DISPOSIÇÕES GERAIS");
        addClauseText(doc, "10.1.", "Quaisquer notificações e comunicações enviadas sob esse contrato podem ser realizadas de forma eletrônica (e-mail ou WhatsApp), escritas ou por correspondência com aviso de recebimento aos endereços constantes do preâmbulo.");
        addClauseText(doc, "10.2.", "Todos os valores, despesas e encargos da locação constituem dívidas líquidas e certas para pagamento à vista, passíveis de cobrança executiva.");
        addClauseText(doc, "10.3.", "Eventuais tolerâncias do LOCADOR para com o LOCATÁRIO no cumprimento das obrigações ajustadas neste contrato constituem mera liberalidade, não importando em hipótese alguma em novação ou renúncia.");
        addClauseText(doc, "10.4.", "O LOCATÁRIO concorda que a sua assinatura no contrato implica ciência e adesão por si, seus herdeiros/sucessores a estas cláusulas.");
        addClauseText(doc, "10.5.", "Fica eleito o foro desta cidade e Comarca de Saquarema/RJ, como competente para dirimir quaisquer questões que possam advir da aplicação do presente contrato.");
        addClauseText(doc, "10.6.", "E, por estarem assim justas e contratadas, as partes firmam o presente instrumento em 02 (duas) vias de igual teor e forma.");
    }

    // -----------------------------------------------------------------------
    // Cláusulas de pagamento — específicas por modalidade
    // -----------------------------------------------------------------------
    private void addPaymentClauses(Document doc, Contract contract) throws DocumentException {
        switch (contract.getRentalType()) {
            case MONTHLY       -> addPaymentClausesMonthly(doc, contract);
            case FIFTEEN_DAYS  -> addPaymentClausesFifteenDays(doc, contract);
            // Adicionar novos tipos aqui no futuro
        }
    }

    /** Modalidade MENSAL — pagamentos semanais antecipados. */
    private void addPaymentClausesMonthly(Document doc, Contract contract) throws DocumentException {
        String weeklyVal = contract.getWeeklyAmount() != null
                ? "R$ " + contract.getWeeklyAmount().toPlainString()
                : "R$ 300,00";
        String depositVal = contract.getDepositAmount() != null
                ? "R$ " + contract.getDepositAmount().toPlainString()
                : "R$ 400,00";

        addSectionHeader(doc, "CLÁUSULA 6ª – DO PAGAMENTO EM RAZÃO DA LOCAÇÃO DO VEÍCULO");
        addClauseText(doc, "6.1.", "O LOCATÁRIO pagará ao LOCADOR o valor de " + weeklyVal + " (semanalmente), sempre de forma antecipada ao período correspondente.");
        addClauseText(doc, "6.2.", "Caso o pagamento seja realizado após a data acordada, o valor sofrerá um acréscimo de R$ 15,00 (quinze reais) a título de multa, bem como um acréscimo de R$ 7,00 (sete reais) por dia de atraso a título de juros.");
        addClauseText(doc, "6.3.", "Fica o LOCATÁRIO obrigado a encaminhar o comprovante de pagamento ao LOCADOR no dia do pagamento, valendo o mesmo como recibo.");

        addDepositClauses(doc, depositVal);
    }

    /** Modalidade QUINZENAL — pagamento integral à vista. */
    private void addPaymentClausesFifteenDays(Document doc, Contract contract) throws DocumentException {
        String fullVal = contract.getWeeklyAmount() != null
                ? "R$ " + contract.getWeeklyAmount().toPlainString()
                : "—";
        String depositVal = contract.getDepositAmount() != null
                ? "R$ " + contract.getDepositAmount().toPlainString()
                : "R$ 400,00";

        addSectionHeader(doc, "CLÁUSULA 6ª – DO PAGAMENTO EM RAZÃO DA LOCAÇÃO DO VEÍCULO");
        addClauseText(doc, "6.1.", "O LOCATÁRIO pagará ao LOCADOR o valor total de " + fullVal + " (quinze dias), de forma integral e antecipada, antes da retirada do veículo.");
        addClauseText(doc, "6.2.", "Não haverá restituição do valor pago em caso de devolução antecipada do veículo, salvo acordo expresso entre as partes.");
        addClauseText(doc, "6.3.", "Fica o LOCATÁRIO obrigado a encaminhar o comprovante de pagamento ao LOCADOR antes da retirada do veículo, valendo o mesmo como recibo.");

        addDepositClauses(doc, depositVal);
    }

    /** Cláusula 7 — caução, comum a todas as modalidades. */
    private void addDepositClauses(Document doc, String depositVal) throws DocumentException {
        addSectionHeader(doc, "CLÁUSULA 7ª – DA QUANTIA CAUÇÃO");
        addClauseText(doc, "7.1.", "Estabelecem as partes a QUANTIA CAUÇÃO no valor total de " + depositVal + ", a ser integralizada até o ato de retirada do veículo.");
        addClauseText(doc, "7.2.", "Ao término da vigência do presente contrato caberá ao LOCADOR restituir a integralidade da QUANTIA CAUÇÃO ao LOCATÁRIO no prazo de 40 (quarenta) dias a contar da devolução do veículo, observadas as condições de: devolução em perfeito estado, inexistência de débitos pendentes, após realização de manutenção necessária e descontados quaisquer outros débitos pendentes.");
        addClauseText(doc, "7.3.", "Na hipótese de não estarem observadas as condições acima, poderá o LOCADOR utilizar-se da QUANTIA CAUÇÃO para adimplir eventuais débitos ou reparar danos causados ao veículo.");
        addClauseText(doc, "7.4.", "Os gastos com o combustível deverão ser arcados integralmente pelo LOCATÁRIO, devendo sempre devolver o veículo com a mesma quantidade de combustível do ato da entrega.");
        addClauseText(doc, "7.5.", "Qualquer valor inerente a cobrança por passagem, estacionamento ou pedágio durante a posse do LOCATÁRIO deverá por este ser arcado.");
        addClauseText(doc, "7.6.", "Caso o veículo seja devolvido sujo, será cobrada a lavagem simples R$ 25,00 (vinte e cinco reais) ou especial R$ 80,00 (oitenta reais), dependendo do estado. Na hipótese de lavagem especial será cobrada também 1 diária de locação.");
    }

    /** Rótulo legível da modalidade para exibir no título do PDF. */
    private String rentalTypeLabel(RentalType type) {
        if (type == null) return "";
        return switch (type) {
            case MONTHLY      -> "Modalidade: Locação Mensal (Pagamentos Semanais)";
            case FIFTEEN_DAYS -> "Modalidade: Locação Quinzenal (Pagamento Integral à Vista)";
            // Adicionar novos tipos aqui no futuro
        };
    }

    // -----------------------------------------------------------------------
    // Helpers de texto
    // -----------------------------------------------------------------------
    private void addClauseText(Document doc, String number, String text) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(number + "  ", FONT_CLAUSE));
        p.add(new Chunk(text, FONT_BODY));
        p.setFirstLineIndent(0);
        p.setIndentationLeft(10);
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private void addSubClause(Document doc, String number, String text) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(number + "  ", FONT_CLAUSE));
        p.add(new Chunk(text, FONT_BODY));
        p.setIndentationLeft(28);
        p.setSpacingAfter(3);
        doc.add(p);
    }

    // -----------------------------------------------------------------------
    // Bloco de assinatura
    // -----------------------------------------------------------------------
    private void addSignatureBlock(Document doc, Contract contract) throws DocumentException {
        String dateStr = contract.getStartDate() != null
                ? contract.getStartDate().format(DATE_FMT)
                : "___________________________";

        Paragraph datePar = new Paragraph("Saquarema, " + dateStr + ".", FONT_SIGN);
        datePar.setAlignment(Element.ALIGN_CENTER);
        datePar.setSpacingBefore(30);
        datePar.setSpacingAfter(40);
        doc.add(datePar);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(signatureCell("_____________________________________\n" + contract.getUser().getName() + "\nLOCATÁRIO"));
        table.addCell(signatureCell("_____________________________________\nMatteus Guimarães Moreno\nLOCADOR"));

        doc.add(table);
    }

    private PdfPCell signatureCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_SIGN));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        return cell;
    }

    // -----------------------------------------------------------------------
    // Utilitários
    // -----------------------------------------------------------------------
    private String formatAddress(Address a) {
        if (a == null) return "—";
        StringBuilder sb = new StringBuilder();
        if (a.getStreet()       != null) sb.append(a.getStreet());
        if (a.getNumber()       != null) sb.append(", ").append(a.getNumber());
        if (a.getComplement()   != null && !a.getComplement().isBlank()) sb.append(", ").append(a.getComplement());
        if (a.getNeighborhood() != null) sb.append(". ").append(a.getNeighborhood());
        if (a.getCity()         != null) sb.append(", ").append(a.getCity());
        if (a.getState()        != null) sb.append(" - ").append(a.getState());
        if (a.getZipCode()      != null) sb.append(". CEP: ").append(a.getZipCode());
        return sb.toString();
    }

    private String maritalStatusPt(MaritalStatus ms) {
        if (ms == null) return "—";
        return switch (ms) {
            case SINGLE   -> "solteiro(a)";
            case MARRIED  -> "casado(a)";
            case DIVORCED -> "divorciado(a)";
            case WIDOWED  -> "viúvo(a)";
            case UNKNOWN  -> "—";
        };
    }

    // -----------------------------------------------------------------------
    // Evento de cabeçalho/rodapé por página
    // -----------------------------------------------------------------------
    private static class HeaderFooterEvent extends PdfPageEventHelper {
        private final String contractId;
        HeaderFooterEvent(String contractId) { this.contractId = contractId; }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Linha divisória superior
            cb.setColorStroke(COLOR_PRIMARY);
            cb.setLineWidth(1.5f);
            cb.moveTo(document.left(), document.top() + 8);
            cb.lineTo(document.right(), document.top() + 8);
            cb.stroke();

            // Rodapé
            cb.setColorStroke(COLOR_BORDER);
            cb.setLineWidth(0.5f);
            cb.moveTo(document.left(), document.bottom() - 12);
            cb.lineTo(document.right(), document.bottom() - 12);
            cb.stroke();

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7, COLOR_BORDER);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Saqua Locamotos – Contrato Nº " + contractId, footerFont),
                    document.left(), document.bottom() - 22, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    new Phrase("Página " + writer.getPageNumber(), footerFont),
                    document.right(), document.bottom() - 22, 0);
        }
    }
}

