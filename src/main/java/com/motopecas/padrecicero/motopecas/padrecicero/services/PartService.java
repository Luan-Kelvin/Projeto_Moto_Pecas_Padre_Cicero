package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartMovementDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPutDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.InventoryMovement;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.PartAlreadyExistsException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.PartNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.InventoryMovementRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.PartRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartService {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    @Lazy
    private StockService stockService;

    @Autowired
    private Conversor conversor;

    // CADASTRAR NOVA PEÇA OBS: USE @AUTHENTICATIONPRINCIPAL PARA PEGAR O FUNCIONARIO QUE FAZ A OPERAÇÃO, ADD NO CONTROLLER)
    @Transactional
    public PartGetDTO registerPart(PartPostDTO dto, Long idEmployee){
        if (partRepository.existsByNameAndMark(dto.name(), dto.mark())){
            throw new PartAlreadyExistsException("ERRO! Peça ja está cadastrada.");
        }

        Employee employee = employeeService.findById(idEmployee);

        String code = generatePartCode();

        Part part = new Part(
                dto.name(),
                dto.application(),
                dto.mark(),
                dto.category(),
                code,
                dto.costPrice(),
                dto.sellingPrice(),
                dto.minimumStock(),
                employee
        );

        Stock stock = new Stock(part);
        stock.addNewQuantityParts(dto.quantity());

        employee.addPart(part);
        partRepository.save(part);
        stockService.saveStock(stock);

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
    public Page<PartGetDTO> searchPartByName(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o que deseja pesquisar."
            );
        }

        String[] words = search.toLowerCase().trim().split("\\s+");

        Specification<Part> specificationExecutor = (root, query, cb) -> {
            List<Predicate> predicate = new ArrayList<>();

            for (String word : words){

                String pattern = "%" + word + "%";

                Predicate name = cb.like(
                        cb.lower(root.get("name")),
                        pattern
                );

                Predicate application = cb.like(
                        cb.lower(root.get("application")),
                        pattern
                );

                Predicate mark = cb.like(
                        cb.lower(root.get("mark")),
                        pattern
                );

                predicate.add(
                        cb.or(name, application, mark)
                );
            }

            return cb.and(predicate.toArray(new Predicate[0]));
        };

        return partRepository.findAll(specificationExecutor, pageable).map(conversor::converterPart);
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

       String newName = dto.name() != null ? dto.name() : part.getName();
       String newMark = dto.mark() != null ? dto.mark() : part.getMark();

       boolean identityChanged = !newName.equals(part.getName()) || !newMark.equals(part.getMark());

        if (identityChanged && partRepository.existsByNameAndMarkAndIdNot(newName, newMark, dto.id())){
            throw new PartAlreadyExistsException("ERRO! Peça ja está cadastrada.");
        }

         if (dto.name() != null && !dto.name().equals(part.getName())){
             part.setName(dto.name());
         }

         if (dto.mark() != null && !dto.mark().equals(part.getMark())){
             part.setMark(dto.mark());
         }

         if (dto.application() != null && !dto.application().equals(part.getApplication())){
            part.setApplication(dto.application());
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

    // CONSULTAR QUANTIDADE EM ESTOQUE
    @Transactional(readOnly = true)
    public Integer checkQuantityPart(Long id){
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException("ERRO! Peça não encontrada."));

        return stockService.checkQuantity(part.getStock().getId());
    }

    // LISTAR HISTÓRICO DE MOVIMENTAÇÕES DA PEÇA
    @Transactional(readOnly = true)
    public Page<PartMovementDTO> listPartMovements(Long id, Pageable pageable){
        partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException(
                        String.format("ERRO! Peça com ID = %s não encontrada.", id)
                ));

        Page<InventoryMovement> movements = inventoryMovementRepository.findByPartId(id, pageable);
        return movements.map(conversor::converterPartMovement);
    }


    private String generatePartCode() {
        partRepository.incrementPartCode();

        Long code = partRepository.generatePartCode();

        return code.toString();
    }
}
