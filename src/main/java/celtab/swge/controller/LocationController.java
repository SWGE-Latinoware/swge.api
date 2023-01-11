package celtab.swge.controller;

import celtab.swge.dto.CepDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.util.ClassPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/location")
public class LocationController implements ClassPathUtils {

    private final RestTemplate restTemplate;

    private final Logger logger;

    public LocationController(RestTemplateBuilder builder) {
        restTemplate = builder.build();
        logger = LoggerFactory.getLogger(LocationController.class);
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/cep/{cep}")
    public Object findCEP(@PathVariable String cep) {
        try {
            var obj = getLocation("https://viacep.com.br/ws/" + cep + "/json/");
            if (obj == null) {
                throw new ControllerException(HttpStatus.BAD_REQUEST, "Location Not Found!");
            }
            var map = (LinkedHashMap<String, Object>) obj;
            var response = new CepDTO();
            if (map.get("erro") != null) {
                response.setError((Boolean) map.get("erro"));
                return response;
            }
            response.setError(false);
            response.setState((String) map.getOrDefault("uf", ""));
            response.setCity((String) map.getOrDefault("localidade", ""));
            response.setDistrict((String) map.getOrDefault("bairro", ""));
            response.setAddress((String) map.getOrDefault("logradouro", ""));
            response.setComplement((String) map.getOrDefault("complemento", ""));
            return response;
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/ufs")
    public Object findUFs() {
        return fetchCacheOnly("ibge/ufs.json");
    }

    @GetMapping("/cities")
    public Object findCities() {
        return fetchCacheOnly("ibge/cities.json");
    }

    private Object fetchCacheOnly(String cacheFileName) throws ControllerException {
        try {
            return getContentFromClassPath(cacheFileName);
        } catch (Exception e) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private Object getLocation(String uri) throws ControllerException {
        try {
            var obj = restTemplate.getForEntity(uri, Object.class);
            logger.info("Resource '{}' Done! Status: {}", uri, obj.getStatusCodeValue());
            if (obj.getStatusCode() == HttpStatus.OK) {
                if (obj.getBody() == null) {
                    throw new ControllerException(HttpStatus.BAD_REQUEST, "Response body is null!");
                }
                return obj.getBody();
            }
            throw new ControllerException(HttpStatus.CONFLICT, "Response status is not OK");
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
