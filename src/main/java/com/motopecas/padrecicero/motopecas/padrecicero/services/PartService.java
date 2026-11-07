package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPutDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.PartAlreadyExistsException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.PartNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PartService {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private Conversor conversor;

    // CADASTRAR NOVA PEÇA OBS: USE @AUTHENTICATIONPRINCIPAL PARA PEGAR O FUNCIONARIO QUE FAZ A OPERAÇÃO, ADD NO CONTROLLER)
    @Transactional
    public PartGetDTO registerPart(PartPostDTO dto, Long idEmployee){
        if(partRepository.existsByNameAndMark(dto.name(), dto.mark())){
            throw new PartAlreadyExistsException("ERRO! Peça ja está cadastrada.");
        }

        Employee employee = employeeService.findById(idEmployee);

        String code = generatePartCode();

        Part part = new Part(
                dto.name(),
                dto.description(),
                dto.mark(),
                dto.category(),
                code,
                dto.costPrice(),
                dto.sellingPrice(),
                dto.minimumStock(),
                employee
        );

        partRepository.save(part);
        employee.addPart(part);

        return conversor.converterPart(part);
    }

    // LISTAR PEÇAS CADASTRADAS COM PAGINAÇÃO
    @Transactional(readOnly = true)
    public Page<PartGetDTO> listParts(Pageable pageable){
        Page<Part> parts = partRepository.findAll(pageable);

        return parts.map(conversor::converterPart);
    }

    // BUSCAR PEÇA PELO ID (CONTROLLER)
    @Transactional(readOnly = true)
    public PartGetDTO findByIdController(Long id){
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException(String.format("ERRO! Peça com ID = %s, não foi enontrda.", id)));

        return conversor.converterPart(part);
    }

    // BUSCAR PEÇA PELO ID (CLASSES)
    @Transactional(readOnly = true)
    public Part findById(Long id){
        return partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException(String.format("ERRO! Peça com ID = %s, não foi enontrda.", id)));

    }

    // EXIBIR TODAS AS PEÇAS QUE CONTENHA O NOME DIGITADO
    @Transactional(readOnly = true)
    public Page<PartGetDTO> searchPartByName(String name) {
        Page<Part> parts = partRepository.findByNameContaining(name);

        return parts.map(conversor::converterPart);
    }

    // ATUALIZAR DADOS DA PEÇA
    @Transactional
    public PartGetDTO updatePart(PartPutDTO dto){
        Part part =  partRepository.findById(dto.id())
                .orElseThrow(() -> new PartNotFoundException(String.format("ERRO! Peça com ID = %s, não foi enontrda.", dto.id())
                ));

        if (dto.costPrice() != null && dto.sellingPrice() == null){
            throw new IllegalArgumentException("ERRO! Para alterar o preço de custo o preço de venda também precisa ser atualizado.");
        }

        if (dto.name() != null && !dto.name().equals(part.getName())){
            part.setName(dto.name());
        }

        if (dto.mark() != null && !dto.mark().equals(part.getMark())){
            part.setMark(dto.mark());
        }

        if (partRepository.existsByNameAndMark(dto.name(), dto.mark())){
            throw new PartAlreadyExistsException("ERRO! Peça ja está cadastrada.");
        }

        if (dto.description() != null && !dto.description().equals(part.getDescription())){
            part.setDescription(dto.description());
        }

        if (dto.category() != null && !dto.category().equals(part.getCategory())){
            part.setCategory(dto.category());
        }

        BigDecimal novoCusto = dto.costPrice() != null
                ? dto.costPrice()
                : part.getCostPrice();

        BigDecimal novoPrecoVenda = dto.sellingPrice() != null
                ? dto.sellingPrice()
                : part.getSellingPrice();

        if (dto.costPrice() != null) {

            if (novoCusto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "O preço de custo deve ser maior que zero."
                );
            }

            part.setCostPrice(novoCusto);
        }

        if (dto.sellingPrice() != null) {

            if (novoPrecoVenda.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "O preço de venda deve ser maior que zero."
                );
            }

            if (novoPrecoVenda.compareTo(novoCusto) <= 0) {
                throw new IllegalArgumentException(
                        "O preço de venda deve ser maior que o preço de custo."
                );
            }

            part.setSellingPrice(novoPrecoVenda);
        }

        if (dto.minimumStock() != null) {

            if (dto.minimumStock() < 0) {
                throw new IllegalArgumentException(
                        "O estoque mínimo não pode ser negativo."
                );
            }

            part.setMinimumStock(dto.minimumStock());
        }

        return conversor.converterPart(part);
    }

    // BUSCAR PEÇAS COM ESTOQUE BAIXO
    @Transactional(readOnly = true)
    public Page<PartGetDTO> searchForItemsWithLowStock(Pageable pageable){
        Page<Part> parts = partRepository.searchForItemsWithLowStock(pageable);

        return parts.map(conversor::converterPart);
    }


    private String generatePartCode() {
        partRepository.incrementPartCode();

        Long code = partRepository.generatePartCode();

        return code.toString();
    }
}
