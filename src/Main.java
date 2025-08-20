//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    private static final String API_KEY = "6ddd2edee966d63878cb005b";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Conversor de Moedas =====");
        System.out.println("Escolha uma opção:");
        System.out.println("1. Real (BRL) -> Dólar (USD)");
        System.out.println("2. Dólar (USD) -> Real (BRL)");
        System.out.println("3. Real (BRL) -> Euro (EUR)");
        System.out.println("4. Euro (EUR) -> Real (BRL)");
        System.out.println("5. Real (BRL) -> Libras Esterlinas (GBP)");
        System.out.println("6. Libras Esterlinas (GBP) -> Real (BRL)");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();

        System.out.print("Digite o valor a converter: ");
        double valor = scanner.nextDouble();

        String deMoeda = "";
        String paraMoeda = "";

        switch (opcao) {
            case 1 -> { deMoeda = "BRL"; paraMoeda = "USD"; }
            case 2 -> { deMoeda = "USD"; paraMoeda = "BRL"; }
            case 3 -> { deMoeda = "BRL"; paraMoeda = "EUR"; }
            case 4 -> { deMoeda = "EUR"; paraMoeda = "BRL"; }
            case 5 -> { deMoeda = "BRL"; paraMoeda = "GBP"; }
            case 6 -> { deMoeda = "GBP"; paraMoeda = "BRL"; }
            default -> {
                System.out.println("Opção inválida!");
                return;
            }
        }

        try {
            double taxa = buscarTaxaCambio(deMoeda, paraMoeda);
            double convertido = valor * taxa;
            System.out.printf("%.2f %s = %.2f %s%n", valor, deMoeda, convertido, paraMoeda);
        } catch (Exception e) {
            System.out.println("Erro ao buscar taxa de câmbio: " + e.getMessage());
        }
    }

    private static double buscarTaxaCambio(String de, String para) throws Exception {
        String url = BASE_URL + API_KEY + "/latest/" + de;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("result") || !json.get("result").getAsString().equals("success")) {
            throw new RuntimeException("Falha na resposta da API: " + response.body());
        }

        JsonObject rates = json.getAsJsonObject("conversion_rates");
        if (!rates.has(para)) {
            throw new RuntimeException("Moeda de destino não encontrada: " + para);
        }
        return rates.get(para).getAsDouble();
    }
}
