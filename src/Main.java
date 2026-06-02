import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // свеча - день торгов
    static class Candle{
        LocalDate date;
        double closePrice;
        public Candle(LocalDate date, double closePrice){
            this.closePrice = closePrice;
            this.date = date;
        }

        // Портфель
        static class Portfolio{
            int shares;
            double cash;
            double initialCapital;
            public Portfolio(double initialCapital){
                this.cash = initialCapital;
                this.shares = 0;
                this.initialCapital = initialCapital;
            }
            // покупка
            void buy(int amount, double price){
                double cost = amount * price;
                if (cash >= cost){
                    cash -= cost;
                    shares += amount;
                    System.out.printf("[BUY] куплено %d акций по цене %.2f. Остаток денег %.2f%n", amount, price, cash);
                } else {
                    System.out.print("[WARN] Недостаточно средств");
                }
            }
            // Продажа
            void sell(int amount, double price){
                if (shares >= amount){
                    double revenue = amount * price;
                    cash += revenue;
                    shares -= amount;
                    System.out.printf("[SELL] Продано %d акций по цене %.2f. Остаток денег: %.2f%n", amount, price, cash);
                } else {
                    System.out.println("[WARN] Недостаточно акций для продажи!");
                }
            }
            // Текущая стоимость портфеля
            public double getTotalValue(double currentPrice) {
                return cash + (shares * currentPrice);
            }
        }
    }
}