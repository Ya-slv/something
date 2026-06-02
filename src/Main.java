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
    // рассчет SMA
    private static double calculateSMA(List<Candle> data, int currentIndex, int period){
        if (currentIndex < period - 1){
            return 0; //недостаточно данных для расчета
        }
        double sum = 0;
        for (int i = 0; i < period; i ++){
            sum += data.get(currentIndex - i).closePrice;
        }
        return sum / period;
    }

    public static void main() {
        List<Candle> marketData = new ArrayList<>();
        marketData.add(new Candle(LocalDate.of(2023, 10, 1), 100.0));
        marketData.add(new Candle(LocalDate.of(2023, 10, 2), 98.0));
        marketData.add(new Candle(LocalDate.of(2023, 10, 3), 96.0));
        marketData.add(new Candle(LocalDate.of(2023, 10, 4), 94.0));  // Дно
        marketData.add(new Candle(LocalDate.of(2023, 10, 5), 95.0));  // Начало роста
        marketData.add(new Candle(LocalDate.of(2023, 10, 6), 98.0));
        marketData.add(new Candle(LocalDate.of(2023, 10, 7), 102.0));
        marketData.add(new Candle(LocalDate.of(2023, 10, 8), 105.0));
        marketData.add(new Candle(LocalDate.of(2023, 10, 9), 108.0));
        marketData.add(new Candle(LocalDate.of(2023, 10, 10), 106.0)); // Небольшая коррекция

        double initialMoney = 10000.0;
        Portfolio portfolio = new Portfolio(initialMoney);

        int fastPeriod = 3; //Быстра средняя
        int slowPeriod = 5; //Медленная средняя
        int tradeSize = 50;

        System.out.println("=== НАЧАЛО БЭКТЕСТИНГА ===");
        System.out.println("Стартовый капитал: $" + initialMoney);
        System.out.println("Стратегия: SMA(" + fastPeriod + ") пересекает SMA(" + slowPeriod + ")\n");

        for (int i = slowPeriod; i < marketData.size(); i++){
            Candle currentCandle = marketData.get(i);
            Candle prevCandle = marketData.get(i-1);

            double currentFastSMA = calculateSMA(marketData, i, fastPeriod);
            double currentSlowSMA = calculateSMA(marketData, i, slowPeriod);
            double prevFastSMA = calculateSMA(marketData, i - 1, fastPeriod);
            double prevSlowSMA = calculateSMA(marketData, i - 1, slowPeriod);

            // Логика стратегии
            // Покупка: раньше быстрая была <= медленной, а теперь стала >
            if (prevFastSMA <= prevSlowSMA && currentFastSMA > currentSlowSMA) {
                System.out.printf("Сигнал BUY! Fast SMA: %.2f -> %.2f, Slow SMA: %.2f -> %.2f%n",
                        prevFastSMA, currentFastSMA, prevSlowSMA, currentSlowSMA);
                portfolio.buy(tradeSize, currentCandle.closePrice);
            }
            // Продажа: раньше быстрая была >= медленной, а теперь стала <
            else if (prevFastSMA >= prevSlowSMA && currentFastSMA < currentSlowSMA) {
                System.out.printf("Сигнал SELL! Fast SMA: %.2f -> %.2f, Slow SMA: %.2f -> %.2f%n",
                        prevFastSMA, currentFastSMA, prevSlowSMA, currentSlowSMA);
                portfolio.sell(tradeSize, currentCandle.closePrice);
            }
        }
        // 5. Финальный расчет
        Candle lastCandle = marketData.get(marketData.size() - 1);
        // Если остались акции, продаем их по последней цене (закрытие позиции)
        if (portfolio.shares > 0) {
            portfolio.sell(portfolio.shares, lastCandle.closePrice);
        }

        double finalValue = portfolio.getTotalValue(lastCandle.closePrice);
        double profit = finalValue - initialMoney;
        double profitPercent = (profit / initialMoney) * 100;

        System.out.println("\n=== РЕЗУЛЬТАТЫ БЭКТЕСТА ===");
        System.out.println("Конечная дата: " + lastCandle.date);
        System.out.println("Стартовый капитал: $" + initialMoney);
        System.out.println("Конечный капитал:  $" + String.format("%.2f", finalValue));
        System.out.println("Чистая прибыль:    $" + String.format("%.2f", profit) + " (" + String.format("%.2f", profitPercent) + "%)");
    }
}
