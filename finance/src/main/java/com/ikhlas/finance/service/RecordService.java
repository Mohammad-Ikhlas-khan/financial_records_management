package com.ikhlas.finance.service;

import com.ikhlas.finance.exception.ResourceNotFoundException;
import com.ikhlas.finance.model.Record;
import com.ikhlas.finance.model.RecordType;
import com.ikhlas.finance.repository.RecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.*;

@Service
public class RecordService {
    @Autowired
    private RecordRepo recordRepo;

    public void recordExists(long recordId){
        Record existingRecord=recordRepo.findById(recordId).orElseThrow(()->new ResourceNotFoundException("Record not found"));
    }

    public void addRecord(Record record){
        recordRepo.save(record);
    }

    public List<Record>  getRecords(
            LocalDate start,LocalDate end,String category,RecordType type
    ){
        if(start!=null && end!=null){
            return recordRepo.findByDateBetween(start,end);
        }
        else if(category!=null){
            return recordRepo.findByCategoryContainingIgnoreCase(category);
        }
        else if(type!=null){
            return recordRepo.findByType(type);
        }
        return recordRepo.findAll();
    }

    public void updateRecord(long recordId,Record record){
        recordExists(recordId);
        record.setRecordId(recordId);
        recordRepo.save(record);
    }

    public void deleteRecord(long recordId){
        recordExists(recordId);
        recordRepo.deleteById(recordId);
    }

    public Map<String,Object> getSummary() {

        double income= recordRepo.getTotalIncome();
        double expense= recordRepo.getTotalExpense();
        Double balance=income-expense;

        //Category Wise Totals
        List<Object[]> categoryData=recordRepo.getCategoryTotals();
        List<Map<String,Object>> categories=new ArrayList<>();
        for(Object[] obj:categoryData){
            Map<String,Object> map=new HashMap<>();
            map.put("Category",obj[0]);
            map.put("income",obj[1]);
            map.put("expense",obj[2]);
            categories.add(map);
        }

        //Recent trends
        List<Record> recent=recordRepo.findTop5ByOrderByDateDesc();

        //Monthly trends
        List<Object[]> trendData=recordRepo.getMonthlyTrend();
        List<Map<String,Object>> trends=new ArrayList<>();

        for(Object[] obj:trendData){
            Map<String,Object> map=new HashMap<>();
            map.put("Month",obj[0]);
            map.put("income",obj[1]);
            map.put("expense",obj[2]);
            trends.add(map);
        }

        Map<String,Object> summary=new HashMap<>();
        summary.put("Total income",income);
        summary.put("Total expenses",expense);
        summary.put("Net balance",balance);
        summary.put("Category wise totals",categories);
        summary.put("Recent Activity",recent);
        summary.put("Monthly trend",trends);

        return summary;
    }
}
