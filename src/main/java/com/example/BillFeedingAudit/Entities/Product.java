package com.example.BillFeedingAudit.Entities;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "rndid",updatable = false,insertable = false)
    private int RndId;

    @Column(updatable = false)
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getRndId() {
        return RndId;
    }

    public void setRndId(int rndId) {
        RndId = rndId;
    }

}
