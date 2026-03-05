package br.com.matteusmoreno.domain.repository;

import br.com.matteusmoreno.application.exception.ExpenseNotFoundException;
import br.com.matteusmoreno.domain.entity.Expense;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

@ApplicationScoped
public class ExpenseRepository implements PanacheMongoRepositoryBase<Expense, String> {

    public Expense findExpenseById(String expenseId) {
        if (!ObjectId.isValid(expenseId)) throw new ExpenseNotFoundException();

        return find("_id", new ObjectId(expenseId)).firstResultOptional()
                .orElseThrow(ExpenseNotFoundException::new);
    }
}
