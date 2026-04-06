package com.ikhlas.finance.repository;

import com.ikhlas.finance.model.Record;
import com.ikhlas.finance.model.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepo extends JpaRepository<Record,Long> {
   List<Record> findByDateBetween(LocalDate start,LocalDate end);
   List<Record> findByCategoryContainingIgnoreCase(String category);
   List<Record> findByType(RecordType type);


   @Query("SELECT SUM(r.amount) FROM Record r where r.type='INCOME'")
   Double getTotalIncome();

    @Query("SELECT SUM(r.amount) FROM Record r where r.type='EXPENSE'")
    Double getTotalExpense();

    @Query(""" 
         SELECT r.category,
         COALESCE(SUM(CASE WHEN r.type='INCOME' THEN r.amount ELSE 0 END),0),
         COALESCE(SUM(CASE WHEN r.type='EXPENSE' THEN r.amount ELSE 0 END),0)
         FROM Record r
         GROUP BY r.category
         """)
    List<Object[]> getCategoryTotals();

    List<Record> findTop5ByOrderByDateDesc();

    @Query("""
            SELECT MONTH(r.date),
            COALESCE(SUM(CASE WHEN r.type='INCOME' THEN r.amount ELSE 0 END),0),
            COALESCE(SUM(CASE WHEN r.type='EXPENSE' THEN r.amount ELSE 0 END),0)
            FROM Record r
            GROUP BY MONTH(r.date)
            """)
    List<Object[]> getMonthlyTrend();
}
