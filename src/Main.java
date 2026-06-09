import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClienteDAO clienteDAO = new ClienteDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO();
        PropostaDAO propostaDAO = new PropostaDAO();
        PagamentoDAO pagamentoDAO = new PagamentoDAO();

        int opcao = -1;

        while (opcao != 0) {
            System.out.println();
            System.out.println("=== Freela Facil ===");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Listar clientes");
            System.out.println("3 - Atualizar email do cliente");
            System.out.println("4 - Excluir cliente");
            System.out.println("5 - Cadastrar projeto");
            System.out.println("6 - Listar projetos com cliente");
            System.out.println("7 - Atualizar status do projeto");
            System.out.println("8 - Excluir projeto");
            System.out.println("9 - Cadastrar proposta");
            System.out.println("10 - Listar propostas com JOIN");
            System.out.println("11 - Aceitar proposta usando procedure");
            System.out.println("12 - Registrar pagamento");
            System.out.println("13 - Calcular total pago usando function");
            System.out.println("14 - Listar view de resumo");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opcao = Integer.parseInt(scanner.nextLine());

            if (opcao == 1) {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Telefone: ");
                String telefone = scanner.nextLine();
                clienteDAO.inserir(nome, email, telefone);
            } else if (opcao == 2) {
                clienteDAO.listar();
            } else if (opcao == 3) {
                System.out.print("ID do cliente: ");
                int id = Integer.parseInt(scanner.nextLine());
                System.out.print("Novo email: ");
                String email = scanner.nextLine();
                clienteDAO.atualizarEmail(id, email);
            } else if (opcao == 4) {
                System.out.print("ID do cliente: ");
                int id = Integer.parseInt(scanner.nextLine());
                clienteDAO.excluir(id);
            } else if (opcao == 5) {
                System.out.print("ID do cliente: ");
                int clienteId = Integer.parseInt(scanner.nextLine());
                System.out.print("Titulo: ");
                String titulo = scanner.nextLine();
                System.out.print("Descricao: ");
                String descricao = scanner.nextLine();
                System.out.print("Orcamento: ");
                double orcamento = Double.parseDouble(scanner.nextLine());
                projetoDAO.inserir(clienteId, titulo, descricao, orcamento);
            } else if (opcao == 6) {
                projetoDAO.listarComCliente();
            } else if (opcao == 7) {
                System.out.print("ID do projeto: ");
                int id = Integer.parseInt(scanner.nextLine());
                System.out.print("Novo status: ");
                String status = scanner.nextLine();
                projetoDAO.atualizarStatus(id, status);
            } else if (opcao == 8) {
                System.out.print("ID do projeto: ");
                int id = Integer.parseInt(scanner.nextLine());
                projetoDAO.excluir(id);
            } else if (opcao == 9) {
                System.out.print("ID do projeto: ");
                int projetoId = Integer.parseInt(scanner.nextLine());
                System.out.print("ID do freelancer: ");
                int freelancerId = Integer.parseInt(scanner.nextLine());
                System.out.print("Valor: ");
                double valor = Double.parseDouble(scanner.nextLine());
                System.out.print("Prazo em dias: ");
                int prazo = Integer.parseInt(scanner.nextLine());
                System.out.print("Mensagem: ");
                String mensagem = scanner.nextLine();
                propostaDAO.inserir(projetoId, freelancerId, valor, prazo, mensagem);
            } else if (opcao == 10) {
                propostaDAO.listarComProjetoEFreelancer();
            } else if (opcao == 11) {
                System.out.print("ID da proposta: ");
                int propostaId = Integer.parseInt(scanner.nextLine());
                propostaDAO.aceitarProposta(propostaId);
            } else if (opcao == 12) {
                System.out.print("ID do contrato: ");
                int contratoId = Integer.parseInt(scanner.nextLine());
                System.out.print("Valor pago: ");
                double valor = Double.parseDouble(scanner.nextLine());
                pagamentoDAO.inserir(contratoId, valor);
            } else if (opcao == 13) {
                System.out.print("ID do contrato: ");
                int contratoId = Integer.parseInt(scanner.nextLine());
                pagamentoDAO.mostrarTotalPago(contratoId);
            } else if (opcao == 14) {
                projetoDAO.listarViewResumo();
            }
        }

        scanner.close();
        System.out.println("Sistema finalizado.");
    }
}
