package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseCreateSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseItemForSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.ItemForSale;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Sale;
import org.springframework.stereotype.Service;

@Service
public class Conversor {

    public EmployeeGetDTO converterEmployee(Employee employee){
        return new EmployeeGetDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getJobType(),
                employee.getActive(),
                employee.getVerified(),
                employee.getCreationDate()
        );
    }

    public PartGetDTO converterPart(Part part){
        return new PartGetDTO(
                part.getId(),
                part.getPartCode(),
                part.getName(),
                part.getDescription(),
                part.getMark(),
                part.getCategory(),
                part.getCostPrice(),
                part.getSellingPrice(),
                part.getActive(),
                part.getCreationDate(),
                part.getCreator().getId(),
                part.getStock().getId()
        );
    }

    public ResponseCreateSaleDTO converterResponseCreateSale(Sale sale){
        return  new ResponseCreateSaleDTO(
                sale.getId(),
                sale.getSaleCode(),
                sale.getDateOfSale(),
                sale.getEmployee().getId(),
                sale.getStatusSale(),
                sale.getTotalValue()
        );
    }

    public ResponseItemForSaleDTO converterItemForSale(ItemForSale item){
        return new ResponseItemForSaleDTO(
                item.getQuantity(),
                item.getUnitPrice(),
                item.getPart().getId(),
                item.getSale().getId(),
                item.getSubtotal()
        );
    }
}
