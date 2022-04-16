package com.example.BillFeedingAudit.Service;

import com.example.BillFeedingAudit.Dao.BatchDao;
import com.example.BillFeedingAudit.Dto.BatchDto;
import com.example.BillFeedingAudit.Entities.Batch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.*;

@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    BatchDao batchDao;

    public static float checkRetMargin(Batch b){
//        float mrp=125;
//        float billprice=100;
        float difference = (float) (b.getMrp()-b.getBillprice());
        float finalresult = (float) ((difference/b.getMrp()) * 100);
        System.out.println(finalresult);
        return finalresult;
    }

    public static boolean checkPercentages(Batch b){
        //  1/5 = 20%
        float result = (float) (b.getMrp()*1/5);
        float finalresult = (float) (b.getMrp()-result);
        System.out.println(b.getProduct().getName());
//        System.out.println("\n result="+result);
//        System.out.println("\n finalresult"+finalresult);
//        System.out.println("\nbillprice"+b.getBillprice());
        if (finalresult == b.getBillprice()){
            return true;
        }else{
            return false;
        }

    }
    
    @Override
    public List<Batch> checkForDiscount(String invoice) {

        Queue<Batch> qlist = new LinkedList<>();
        int count=0;
        qlist.addAll(batchDao.findBypurfeednoIgnoreCase(invoice));
        List<Batch> blist2 = new ArrayList<>();
        System.out.println("test"+qlist);
        for(Batch q : qlist){
            boolean result = checkPercentages(q);

            if(result == true){
                continue;
            }else{
                blist2.add(q);
                count ++;
                System.out.println("<<<<<<<<<< products with wrong retail margin >>>>>>>>>"+q.getProduct().getName()+" "+ q.getRetmargin()+ "->"+checkRetMargin(q));
            }
        }
        System.out.println(count);
        System.out.println("<<<<<<<<<<<<<<<< final list >>>>>>>>>>>>>>>>\n"+blist2);

        return blist2;
    }


    public Batch getNewBatch(long rndid){
        return batchDao.getRecordById(rndid);
    }

    public Batch getOldBatch(String invoice,String prodid,Batch newBatch){
        Queue<Batch> qlist = new LinkedList<>();
        Batch oldBatch=null;
        qlist.addAll(batchDao.productListSortByDate(prodid));


        for (int i=0 ; i<= qlist.size();i++) {
            oldBatch=null;
            Batch batchQueue = qlist.poll();

            if (batchQueue.getPurfeedno().equals(invoice)==false && batchQueue.getPurdate().before(newBatch.getPurdate()) && batchQueue.getBatchno().equals(newBatch.getBatchno())==false)
            {
                oldBatch=batchQueue;
                qlist.clear();
                break;
            }

        }
        return oldBatch;
    }

    @Override
    public List<Batch> batchDetails(String invoice) {

        Batch newBatch=null;
        Batch oldBatch=null;

        List<Batch> comparedBatches = new ArrayList<>();

        //contains productlist of invoice no. which is entered in frontend
        List<Batch> blist = batchDao.findBypurfeednoIgnoreCase(invoice);
        System.out.println("Batch by Invoice: " + blist);

        for(Batch b : blist){
              newBatch=getNewBatch(b.getRndId());


            System.out.println("new Batch from function\n"+newBatch);

            oldBatch=getOldBatch(invoice, b.getProdid(),newBatch);
            System.out.println("old Batch from function\n"+oldBatch);

            if (oldBatch!=null && newBatch!=null){
                if (oldBatch.getMrp() != newBatch.getMrp() && oldBatch.getPurdate().before(newBatch.getPurdate())) {
                    System.out.println(newBatch.getProdid());
                    System.out.println("-------------------------------difference in batch Mrp's------------------------------");
                    System.out.println("newbatch" + newBatch);
                    System.out.println("oldbatch" + oldBatch);
                     comparedBatches.add(newBatch);
                     comparedBatches.add(oldBatch);
                }
            }
        }

        return comparedBatches;
    }

}