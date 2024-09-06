package io.minicap.covid19trackingApp.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.repository.PatientRepository;

@Service("patientService")
public class PatientService 
{
    @Autowired
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository)
    {
        this.patientRepository = patientRepository;
    }

    public JSONObject produceJSONforNetworkGraph(Patient patient)
    {
        ArrayList <Patient> listNodes = getAllNodes(patient);
        ArrayList <Patient []> listEdges = getAllEdges(patient);

        JSONObject elements = new JSONObject();
        JSONArray nodes = new JSONArray();
        JSONArray edges = new JSONArray();
        
        ArrayList <Patient> to = getNodesTo(patient);

        for(Patient a : listNodes)
        {
            JSONObject style = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject data2 = new JSONObject();

            data.put("id", a.getFirstName());

            data.put("idNumber", a.getId());
            
            if(a.isIsPositive())
            {
                style.put("background-color", "orange");     
            }

            if(a.getInfectionStatus() == infectionStatus.contactTraced)
            {
                style.put("background-image", "../../contactTraceAssets/img/avatars/Blue_Pokeball.jpeg");
                style.put("background-fit", "cover cover");       
            }

            if(a.getInfectionStatus() == infectionStatus.asymptomatic)
            {
                style.put("background-image", "../../contactTraceAssets/img/avatars/YellowBlack_Pokeball.jpeg");
                style.put("background-fit", "cover cover");       
            }

            if(a.getInfectionStatus() == infectionStatus.symptomatic)
            {
                style.put("background-image", "../../contactTraceAssets/img/avatars/OrangeBlack_Pokeball.jpeg");
                style.put("background-fit", "cover cover");       
            }

            if(a.getInfectionStatus() == infectionStatus.critical)
            {
                style.put("background-image", "../../contactTraceAssets/img/avatars/RedBlack_Pokeball.jpeg");
                style.put("background-fit", "cover cover");       
            }

            if(a.getInfectionStatus() == infectionStatus.none)
            {
                style.put("background-image", "../../contactTraceAssets/img/avatars/Green_Pokeball.jpeg");
                style.put("background-fit", "cover cover");       
            }

            if(a.getId() == patient.getId())
            {
                style.put("border-width", "3");
                style.put("text-outline-color", "blue");
                style.put("font-size", "7");
                style.put("border-color", "black");   
            }

            if(a.isIsFlagged())
            {
                style.put("border-width", "2");
                style.put("border-color", "red");
            }

            if(to.contains(a))
            {
                data.put("href", "/contact-trace/" + a.getId());
                style.put("border-width", "2");
                style.put("border-color", "black"); 
            }
            
            data2.put("data", data);

            data2.put("style", style);

            nodes.put(data2);

            
        }

        for(Patient [] a : listEdges)
        {
            JSONObject data = new JSONObject();
            JSONObject data2 = new JSONObject();
            JSONObject style = new JSONObject();

            data.put("id", a[0].getFirstName()+a[1].getFirstName());
            data.put("source", a[0].getFirstName());
            data.put("target", a[1].getFirstName());

            if(to.contains(a[0]) || to.contains(a[1]))
            {
                style.put("line-color", "black"); 
                style.put("target-arrow-color", "black");  
            }

            if(a[0].getId() == patient.getId())
            {
                style.put("line-color", "blue"); 
                style.put("width", "3"); 
                style.put("target-arrow-color", "blue");
            }

            data2.put("data", data);
            data2.put("style", style);

            edges.put(data2);
        }

        elements.put("nodes", nodes);
        elements.put("edges", edges);

        return elements;
    }

    public ArrayList<Patient> getAllNodes(Patient patient)
    {
        ArrayList<Patient> allNodes = getNodesTo(patient);

        allNodes.addAll(getNodesFrom(patient));

        allNodes.add(patient);

        return allNodes;
    }

    public ArrayList<Patient[]> getAllEdges(Patient patient)
    {
        ArrayList<Patient []> allEdges = getEdgesTo(patient);

        allEdges.addAll(getEdgesFrom(patient));

        return allEdges;
    }


    public ArrayList<Patient> getNodesTo(Patient patient)
    {

        List <Patient> contactTraced = patientRepository.findByContactTraceTo(patient);
        ArrayList<Patient> nodes = new ArrayList<Patient>();

        if(contactTraced == null)
        {
          return null;  
        }

        for(Patient a : contactTraced)
        {
            if(nodes.contains(a))
            {
                continue;
            }

            nodes.add(a);
            nodes.addAll(getNodesTo(a));
        }
        
        return nodes;
    }

    public ArrayList<Patient> getNodesFrom(Patient patient)
    {

        List <Patient> contactTracedFrom = patientRepository.findByContactTraceFrom(patient);

        ArrayList<Patient> nodes = new ArrayList<Patient>();

        if(contactTracedFrom == null)
        {
          return null;  
        }

        for(Patient a : contactTracedFrom)
        {
            if(nodes.contains(a))
            {
                continue;
            }

            nodes.add(a);
            nodes.addAll(getNodesFrom(a));
        }
        
        return nodes;
    }

    public ArrayList<Patient []> getEdgesFrom(Patient patient)
    {

        List <Patient> contactTraced = patientRepository.findByContactTraceTo(patient);
        
        ArrayList<Patient[]> edges = new ArrayList<Patient[]>();

        if(patient == null)
        {
          return null;  
        }

        for(Patient a : contactTraced)
        {
            Patient[] edgeArray  = new Patient[2];

            edgeArray[0] =  a;
            edgeArray[1] = patient;

            if(edges.contains(edgeArray))
            {
                continue;
            }

            edges.add(edgeArray);
            edges.addAll(getEdgesFrom(a));
            
        }

        return edges;
    }

    public ArrayList<Patient []> getEdgesTo(Patient patient)
    {

        List <Patient> contactTracedFrom = patientRepository.findByContactTraceFrom(patient);
        
        ArrayList<Patient[]> edges = new ArrayList<Patient[]>();

        if(patient == null)
        {
          return null;  
        }

        for(Patient a : contactTracedFrom)
        {

            Patient[] edgeArray  = new Patient[2];

            edgeArray[0] =  patient;
            edgeArray[1] = a;

            if(edges.contains(edgeArray))
            {
                continue;
            }

            edges.add(edgeArray);
            edges.addAll(getEdgesTo(a));
        }

        return edges;
    }    
}
