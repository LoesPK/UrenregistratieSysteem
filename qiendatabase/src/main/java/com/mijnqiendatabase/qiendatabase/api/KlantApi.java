package com.mijnqiendatabase.qiendatabase.api;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mijnqiendatabase.qiendatabase.domain.Klant;
import com.mijnqiendatabase.qiendatabase.domain.Trainee;
import com.mijnqiendatabase.qiendatabase.domain.Uur;
import com.mijnqiendatabase.qiendatabase.service.KlantService;
import com.mijnqiendatabase.qiendatabase.service.TraineeService;


@Path("klant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class KlantApi {

	@Autowired
	private KlantService klantService;
	@Autowired
	private TraineeService traineeService;
	
	@POST // Create
	public Response apiCreate(Klant klant) {
		if (klant.getId() != 0) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		return Response.ok(klantService.save(klant)).build();
		
	}

	@GET // Retrieve/Read
	@Path("{id}")
	public Response apiGetById(@PathParam("id") long id) {
		Optional<Klant> klant = klantService.findById(id);
		if (klant.isPresent() == false) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			return Response.ok(klant.get()).build();
		}
	}
	
	@GET // Retrieve/Read
  	public Response apiGetAll() {
         	return Response.ok(klantService.findAll()).build();
  	}
	
	@PUT // Update Klant van een trainee
  	@Path("naw/{id}")
  	public Response apiUpdateNAW(@PathParam("id") long id, Klant klant) {
  		System.out.println("in PUT  Klant " + klant.getId());
         	if (klant == null || klant.getId() != id) {
               	System.out.println("bad request?");
         		return Response.status(Response.Status.BAD_REQUEST).build();
         	}
         	Optional<Klant> optionalOldKlant = klantService.findById(klant.getId());
         	if (!optionalOldKlant.isPresent()) {
         		System.out.println("not found?");
               	return Response.status(Response.Status.NOT_FOUND).build();
         	}
         	
         	Klant target = optionalOldKlant.get();
         	target.setVoornaam(klant.getVoornaam());
         	target.setAchternaam(klant.getAchternaam());
         	//target.setWachtwoord(trainee.getWachtwoord());
         	target.setEmailadres(klant.getEmailadres());
         	target.setUsername(klant.getUsername());
         	target.setBedrijf(klant.getBedrijf());
         	System.out.println("check2 in klant naw");
    

         	return Response.ok(klantService.save(target)).build();
  	}
 
	@PUT // Update Klant zn trainee
  	@Path("{id}")
  	public Response apiUpdate(@PathParam("id") long id, Klant klant) {
  		System.out.println("in PUT  Klant " + klant.getId());
         	if (klant == null || klant.getId() != id) {
               	System.out.println("bad request?");
         		return Response.status(Response.Status.BAD_REQUEST).build();
         	}
         	Optional<Klant> oldKlant = klantService.findById(klant.getId());
         	if (!oldKlant.isPresent()) {
         		System.out.println("not found?");
               	return Response.status(Response.Status.NOT_FOUND).build();
         	}
         	Set<Trainee> nieuwetrainee = new HashSet();
         	nieuwetrainee = oldKlant.get().getTrainee();
         	
         	for(Trainee trainee : klant.getTrainee()) {
         		trainee.setKlant(klant);         		
         		nieuwetrainee.add(traineeService.save(trainee));
         		
         	}
         	klant.setTrainee(nieuwetrainee);
         	Klant target = oldKlant.get();
         	target.setTrainee(klant.getTrainee());
    

         	return Response.ok(klantService.save(target)).build();
  	}
	
	@PUT // Update door een trainee te verwijderen
	@Path("min/{id}/{traineeId}")
	public Response apiRemoveTrainee(@PathParam("id") long klantId, @PathParam("traineeId") long traineeId) {
		System.out.println("Check in KlantPUTTrainee");
		
		Optional<Trainee> optTrainee = traineeService.findById(traineeId);
		if (!optTrainee.isPresent()) {
			System.out.println("bad request?");
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		Optional<Klant> optKlant = klantService.findById(klantId);
		if (!optKlant.isPresent()) {
			System.out.println("not found?");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
//		if (!optKlant.get().removeTrainee(optTrainee.get())) {
//			System.out.println("not modified?");
//			return Response.status(Response.Status.NOT_MODIFIED).build();
//		}
		System.out.println("Endcheck in PUTKlantApi");
		return Response.ok(klantService.save(optKlant.get())).build();
	}
	
 
  	@DELETE // Delete
  	@Path("{id}")
  	public Response apiDeleteById(@PathParam("id") long id) {
         	if (klantService.findById(id).isPresent() == false) {
               	return Response.status(Response.Status.NOT_FOUND).build();
         	} else {
               	klantService.deleteById(id);
               	return Response.status(Response.Status.OK).build();
         	}
  	}

}