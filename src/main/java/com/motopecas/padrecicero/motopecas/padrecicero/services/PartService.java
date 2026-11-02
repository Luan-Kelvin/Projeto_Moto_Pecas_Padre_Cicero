package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPostDTO;
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

@Service
public class PartService {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private Conversor conversor;

    // CADASTRAR NOVA PEÇA
    @Transactional
    public PartGetDTO registerPart(PartPostDTO dto){
        if(partRepository.existsByNameAndMark(dto.name(), dto.mark())){
            throw new PartAlreadyExistsException("ERRO! Peça ja está cadastrada.");
        }

        Employee creator = employeeService.findById(dto.creatorId());

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
                creator
        );

        partRepository.save(part);

        return conversor.converterPart(part);
    }

    // LISTAR PEÇAS CADASTRADAS COM PAGINAÇÃO
    @Transactional
    public Page<PartGetDTO> listParts(Pageable pageable){
        Page<Part> parts = partRepository.findAll(pageable);

        return parts.map(conversor::converterPart);
    }

    // BUSCAR PEÇA PELO ID
    @Transactional
    public PartGetDTO findById(Long id){
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException(String.format("ERRO! Peça com ID = %s, não foi enontrda.", id)));

        return conversor.converterPart(part);
    }


    @Transactional
    private String generatePartCode() {
        partRepository.incrementPartCode();

        Long code = partRepository.generatePartCode();

        return code.toString();
    }
}
