package com.proto.BIS.common.Controller;

import com.Proto.demo.common.Model.ServicesModel;
import com.Proto.demo.common.Service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ServiceController {

    @Autowired
    WorkService service;

    @RequestMapping("/services")
    @ResponseBody
    public List<ServicesModel> getServices(){
        return service.getProducts();
    }

    @RequestMapping("/service/{serId}")
    @ResponseBody
    public ServicesModel getServiceById(@PathVariable int serId){
        return service.getProductsById(serId);
    }

    @PostMapping("/addService")
    @ResponseBody
    public void addService(@RequestBody ServicesModel model){
        service.addProduct(model);
    }

    @PutMapping("/updateService")
    @ResponseBody
    public void updateService(ServicesModel model){
        service.updateProduct(model);
    }

    @DeleteMapping("/deleteService/{serId}")
    @ResponseBody
    public void deleteProduct(@PathVariable int serId){
        service.deleteProduct(serId);
    }
}
