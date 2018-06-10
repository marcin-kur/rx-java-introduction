package com.virtuslab.rx_intro.task;

import io.reactivex.Observable;
import io.reactivex.Single;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * ConfirmedTransactionSummarizer is responsible for calculation of total confirmed transactions value.
 * HINT:
 * - Use zip operator to match transactions with confirmations. They will appear in order
 * - Filter only confirmed
 * - Aggregate value of confirmed transactions using reduce operator
 * <p>
 * HINT2:
 * - add error handling which will wrap an error into SummarizationException
 */
class ConfirmedTransactionSummarizer {

    private final Supplier<Observable<Transaction>> transactions;
    private final Supplier<Observable<Confirmation>> confirmations;

    ConfirmedTransactionSummarizer(Supplier<Observable<Transaction>> transactions,
                                   Supplier<Observable<Confirmation>> confirmations) {
        this.transactions = transactions;
        this.confirmations = confirmations;
    }

    Single<BigDecimal> summarizeConfirmedTransactions() {
        return transactions.get().zipWith(confirmations.get(), Pair::new)
                .filter(pair -> pair.confirmation.isConfirmed)
                .reduce(BigDecimal.ZERO, (result, pair) -> result.add(pair.transaction.value))
                .onErrorResumeNext(error -> Single.error(new SummarizationException(error.getMessage())));
    }

    static class SummarizationException extends RuntimeException {

        public SummarizationException(String message) {
            super(message);
        }
    }

    class Pair {
        final Transaction transaction;
        final Confirmation confirmation;

        Pair(Transaction transaction, Confirmation confirmation) {
            this.transaction = transaction;
            this.confirmation = confirmation;
        }
    }
}
