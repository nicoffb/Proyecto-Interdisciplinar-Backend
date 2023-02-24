package com.salesianostriana.gamesforall.product.controller;

import com.salesianostriana.gamesforall.files.service.StorageService;
import com.salesianostriana.gamesforall.files.utils.MediaTypeUrlResource;
import com.salesianostriana.gamesforall.product.dto.BasicProductDTO;
import com.salesianostriana.gamesforall.product.dto.EasyProductDTO;
import com.salesianostriana.gamesforall.product.dto.PageDto;
import com.salesianostriana.gamesforall.product.dto.ProductRequestDTO;
import com.salesianostriana.gamesforall.product.model.Product;
import com.salesianostriana.gamesforall.product.service.ProductService;
import com.salesianostriana.gamesforall.search.util.Extractor;
import com.salesianostriana.gamesforall.search.util.SearchCriteria;
import com.salesianostriana.gamesforall.user.model.User;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final StorageService storageService;


    //buscar tooooodos
    @GetMapping("/search")
    public PageDto<EasyProductDTO> getByCriteria(@RequestParam(value = "search", defaultValue = "") String search,
                                          @PageableDefault(size = 5, page = 0) Pageable pageable) {

        List<SearchCriteria> params = Extractor.extractSearchCriteriaList(search);
        PageDto<EasyProductDTO> products = productService.search(params, pageable);

        return products;

    }


//    @Operation(summary = "Obtiene un producto a partir de un id dado")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200",
//                    description = "Se ha encontrado el producto",
//                    content = {
//                            @Content(mediaType = "application/json",
//                                    content = @Content(schema = @Schema(implementation = BasicProductDTO.class))),
//                    }),
//            @ApiResponse(responseCode = "404",
//                    description = "No se ha encontrado el producto",
//                    content = @Content(schema = @Schema(implementation = com.salesianostriana.gamesforall.exception.ProductNotFoundException.class))),
//            @ApiResponse(responseCode = "400",
//                    description = "Los datos introducidos son incorrectos",
//                    content = @Content),
//    })
    @GetMapping("/{id}")
    public BasicProductDTO getById(@PathVariable Long id) {

        return productService.findById(id);
    }



    @PostMapping("/")
    public ResponseEntity<BasicProductDTO> createNewProduct(@RequestPart("body") ProductRequestDTO created, @RequestPart("files") MultipartFile files) {


        Product product =created.toProduct(created); //estabien invocarlo con created?

        productService.add(product,files);

        URI createdURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId()).toUri();

        BasicProductDTO converted = BasicProductDTO.of(product);

        return ResponseEntity
                .created(createdURI)
                .body(converted);

        //gestionar el fallo con bad request o manejo de errores
    }


    @PutMapping("/{id}")
    public BasicProductDTO editProduct(@PathVariable Long id, @RequestBody BasicProductDTO edited) {
        return productService.edit(id,edited);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.deleteById(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename){
        MediaTypeUrlResource resource =
                (MediaTypeUrlResource) storageService.loadAsResource(filename);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", resource.getType())
                .body(resource);
}

//un controller para eliminar foto? y editar foto solo? o meterlo en el de editar producto



}

