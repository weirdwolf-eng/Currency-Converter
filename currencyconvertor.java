import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter extends JFrame {
    private JTextField amountField;
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JLabel resultLabel;
    private JButton convertButton;
    private JButton swapButton;
    private JLabel lastUpdateLabel;
    
    private Map<String, Double> exchangeRates;
    private final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    
    // Popular currencies
    private final String[] currencies = {
        "USD - US Dollar",
        "EUR - Euro",
        "GBP - British Pound",
        "JPY - Japanese Yen",
        "INR - Indian Rupee",
        "AUD - Australian Dollar",
        "CAD - Canadian Dollar",
        "CHF - Swiss Franc",
        "CNY - Chinese Yuan",
        "AED - UAE Dirham",
        "SGD - Singapore Dollar",
        "MXN - Mexican Peso",
        "BRL - Brazilian Real",
        "ZAR - South African Rand",
        "KRW - South Korean Won"
    };
    
    public CurrencyConverter() {
        setTitle("Currency Converter");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        exchangeRates = new HashMap<>();
        
        initComponents();
        fetchExchangeRates();
    }
    
    private void initComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Title
        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(25, 25, 112));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Amount Panel
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.setBackground(new Color(240, 248, 255));
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // From Currency Panel
        JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fromPanel.setBackground(new Color(240, 248, 255));
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fromCurrency = new JComboBox<>(currencies);
        fromCurrency.setFont(new Font("Arial", Font.PLAIN, 12));
        fromPanel.add(fromLabel);
        fromPanel.add(fromCurrency);
        mainPanel.add(fromPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Swap Button Panel
        JPanel swapPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        swapPanel.setBackground(new Color(240, 248, 255));
        swapButton = new JButton("⇅ Swap");
        swapButton.setFont(new Font("Arial", Font.BOLD, 12));
        swapButton.setBackground(new Color(100, 149, 237));
        swapButton.setForeground(Color.WHITE);
        swapButton.setFocusPainted(false);
        swapButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        swapButton.addActionListener(e -> swapCurrencies());
        swapPanel.add(swapButton);
        mainPanel.add(swapPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // To Currency Panel
        JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toPanel.setBackground(new Color(240, 248, 255));
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toCurrency = new JComboBox<>(currencies);
        toCurrency.setSelectedIndex(1); // Default to EUR
        toCurrency.setFont(new Font("Arial", Font.PLAIN, 12));
        toPanel.add(toLabel);
        toPanel.add(toCurrency);
        mainPanel.add(toPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Convert Button
        convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(new Color(34, 139, 34));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        convertButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        convertButton.addActionListener(e -> performConversion());
        mainPanel.add(convertButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Result Panel
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
        resultPanel.setMaximumSize(new Dimension(450, 80));
        
        resultLabel = new JLabel("Enter amount and click Convert");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(new Color(25, 25, 112));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        resultPanel.add(resultLabel);
        resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        mainPanel.add(resultPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Last Update Label
        lastUpdateLabel = new JLabel("Fetching exchange rates...");
        lastUpdateLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        lastUpdateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lastUpdateLabel.setForeground(Color.GRAY);
        mainPanel.add(lastUpdateLabel);
        
        add(mainPanel);
        
        // Enter key listener for amount field
        amountField.addActionListener(e -> performConversion());
    }
    
    private void fetchExchangeRates() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .build();
                    
                    HttpResponse<String> response = client.send(request, 
                            HttpResponse.BodyHandlers.ofString());
                    
                    JSONObject json = new JSONObject(response.body());
                    JSONObject rates = json.getJSONObject("rates");
                    
                    // Store exchange rates
                    for (String currency : currencies) {
                        String code = currency.split(" - ")[0];
                        if (rates.has(code)) {
                            exchangeRates.put(code, rates.getDouble(code));
                        }
                    }
                    
                    // Add USD base rate
                    exchangeRates.put("USD", 1.0);
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CurrencyConverter.this,
                                "Error fetching exchange rates: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
            
            @Override
            protected void done() {
                if (!exchangeRates.isEmpty()) {
                    lastUpdateLabel.setText("Exchange rates updated successfully");
                    convertButton.setEnabled(true);
                } else {
                    lastUpdateLabel.setText("Failed to fetch exchange rates");
                    convertButton.setEnabled(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void performConversion() {
        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter an amount",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            
            if (amount < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a positive amount",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String fromCode = fromCurrency.getSelectedItem().toString().split(" - ")[0];
            String toCode = toCurrency.getSelectedItem().toString().split(" - ")[0];
            
            // Convert from source currency to USD, then to target currency
            double amountInUSD = amount / exchangeRates.get(fromCode);
            double convertedAmount = amountInUSD * exchangeRates.get(toCode);
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            resultLabel.setText(df.format(amount) + " " + fromCode + " = " + 
                              df.format(convertedAmount) + " " + toCode);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Conversion error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void swapCurrencies() {
        int fromIndex = fromCurrency.getSelectedIndex();
        int toIndex = toCurrency.getSelectedIndex();
        
        fromCurrency.setSelectedIndex(toIndex);
        toCurrency.setSelectedIndex(fromIndex);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            CurrencyConverter converter = new CurrencyConverter();
            converter.setVisible(true);
        });
    }
}
