package com.rbkmoney.deanonimus.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "party")
@Setting(settingPath = "/settings/autocomplete-analyzer.json")
public class Party {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
    private String email;

    private Blocking blocking;
    private Suspension suspension;

    @Field(type = FieldType.Nested, store = true)
    private List<Contractor> contractors;
    @Field(type = FieldType.Nested, store = true)
    private List<Contract> contracts;
    @Field(type = FieldType.Nested, store = true)
    private List<Shop> shops;

    public void addShop(Shop shop) {
        if (this.shops == null) {
            this.shops = new ArrayList<>();
        }
        this.shops.add(shop);
    }

    public void addContract(Contract contract) {
        if (this.contracts == null) {
            this.contracts = new ArrayList<>();
        }
        this.contracts.add(contract);
    }

    public void addContractor(Contractor contractor) {
        if (this.contractors == null) {
            this.contractors = new ArrayList<>();
        }
        this.contractors.add(contractor);
    }

    public Optional<Shop> getShopById(String id) {
        return this.shops.stream().filter(shop -> shop.getId().equals(id)).findFirst();
    }

    public Optional<Contract> getContractById(String id) {
        return this.contracts.stream().filter(contract -> contract.getId().equals(id)).findFirst();
    }

    public Optional<Contractor> getContractorById(String id) {
        return this.contractors.stream().filter(contractor -> contractor.getId().equals(id)).findFirst();
    }

}
