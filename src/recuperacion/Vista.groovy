/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package recuperacion

import groovy.swing.*;
import javax.swing.table.DefaultTableModel;
import groovy.model.ValueHolder;
import java.util.*;
import recuperacion.JPA.*;
import recuperacion.exceptions.*;
import java.util.logging.Logger;
import org.apache.commons.math.linear.SingularValueDecompositionImpl;
import javax.persistence.EntityManager;
import javax.swing.*;

/**
 *
 * @author octavioruizcastillo
 */
public class Vista {
    DefaultTableModel modeloDocs       = new DefaultTableModel();
    DefaultTableModel modeloFrec       = new DefaultTableModel();
    DefaultTableModel modeloConsultas  = new DefaultTableModel();
    DefaultTableModel modeloSVD        = new DefaultTableModel();
    DefaultTableModel modeloResultados = new DefaultTableModel();
    Gui g;

    def gui() {
        def swing = SwingBuilder.build {
            //Inicialización de la vista 1/2
            lookAndFeel( 'nimbus' )
            g = new Gui(title:"Recuperación de información")
            g.show true

            //Acción del botón ---[Guardar e indexar documento]---
            g.btnIndexar.actionPerformed = {
                indexa(g.doc.text)
            }
            //Acción del botón ---[Guardar K]---
            g.btnGuardarIndice.actionPerformed = {
                Indexador idx = new Indexador()
                idx.guardarIndices(g.mayor.text as int ,g.optimo.text as int,g.menor.text as int)
            }

            g.btnCargar.actionPerformed = {
                cargaEIndexa(selectFile())
            }

            //Acción del botón ---[Buscar]---
            g.btnBuscar.actionPerformed = {
                
                def idConsulta
                if(!g.txtConsulta.text.equals("")) {
                    idConsulta = createConsulta(g.txtConsulta.text)
                } else {
                    def selectedRowInt = g.tabConsultas.getSelectedRow()
                    idConsulta = g.tabConsultas.getValueAt(selectedRowInt, 0) as Integer
                }

                switch(g.tipoConsulta.getSelectedValue()) {
                    case "Euclidiana":
                        buscarEuclidiana(idConsulta); break;
                    case "Coseno":
                        buscarCoseno(idConsulta); break;
                    case "Coef. Dice":
                        buscarDice(idConsulta); break;
                    case "Coseno LSI":
                        buscarCosenoLSI(g.listK.getSelectedValue(), idConsulta); break;
                    case "Manhattan LSI":
                        buscarManhattanLSI(g.listK.getSelectedValue(), idConsulta); break;
                    case "Euclidiana LSI":
                        buscarEuclidianaLSI(g.listK.getSelectedValue(), idConsulta); break;
                    case "Producto Interno LSI":
                        buscarProductoInternoLSI(g.listK.getSelectedValue(), idConsulta); break;
                }
                
                g.txtConsulta.text = ""
            }
            //Inicialización de la vista 2/2
            g.tabDocs.setModel(modeloDocs)
            g.tabFrecs.setModel(modeloFrec)
            g.tablaMatrizDiagonal.setModel(modeloSVD)
            g.tabConsultas.setModel(modeloConsultas)
            g.tabResultados.setModel(modeloResultados)
            updateUI()
        }
    }


    void indexa(String txt) {
        DocumentoJpaController jpa = new DocumentoJpaController();
        Documento d = new Documento();
        d.setContenido(txt);
        d.setTipo("DOCUMENTO")
        jpa.create(d);
        Indexador idx = new Indexador();
        idx.setDoc(d);
        idx.indexar();
        updateUI()
    }
    void cargaEIndexa(String path) {
        Indexador idx = new Indexador();
        idx.guardarDocumento(new File(path))
        idx.indexar()
        updateUI()
    }

    def createConsulta(String txt) {
        Documento d = new Documento();
        d.setContenido(txt);
        d.setTipo("CONSULTA")
        Recuperador r = new Recuperador();
        r.crearConsulta(d);
        updateUI()
        return r.doc.getId()
    }

    def selectFile()
    {
        JFileChooser fc = new JFileChooser()
        fc.showOpenDialog(null)
        return fc.getSelectedFile().path
    }

    def updateUI() {
        updateUIDocs()
        updateUIFrecs()
        updateUIConsultas()
        updateUISVD()
    }

    def updateUIConsultas() {
        Recuperador r = new Recuperador()
        def consultas = r.findAllConsultas()

        String[][] a = consultas
        String[] b = ["ID", "Consulta"]

        modeloConsultas.setDataVector(a,b)
    }

    def updateUIDocs() {
        DocumentoJpaController jpa = new DocumentoJpaController();
        def docs = [];
        jpa.findDocumentos().each() {
            docs << [it.getId(), it.getContenido()]
        }
        String[][] a = docs;

        String[] b = ["ID", "Contenido"];
        
        modeloDocs.setDataVector(a, b)
    }
    
    def updateUIFrecs() {
        DocumentoJpaController jpa      = new DocumentoJpaController();

        String[] b = ["Término"]+jpa.findDocumentoEntities()*.id;
        def frecs = [:];
        def frecsList = []

        Indexador idx = new Indexador();
        frecsList = idx.getTablaFrecuencias(true)
        Object[][] a = frecsList;

        modeloFrec.setDataVector(a, b)
    }

    def updateUISVD() {
        //-----Actualizar matriz diagonal
        Indexador idx = new Indexador()
        SingularValueDecompositionImpl svd = idx.descomposicionSVD();

        if(svd != null) {
            def docs = [];
            svd.getS().getData().each() {
                docs << it
            }
            String[][] a = docs;
            String[] b = 1..docs.size();

            modeloSVD.setDataVector(a,b)
        }

        //-----Actualizar k
        MatrizSVDJpaController jpa = new MatrizSVDJpaController();
        g.mayor .text = jpa.findIndices("mayor" )?.getK()
        g.optimo.text = jpa.findIndices("optimo")?.getK()
        g.menor .text = jpa.findIndices("menor" )?.getK()
    }

    def buscarEuclidiana(Integer idConsulta) {
        Recuperador r = new Recuperador()
        def consultas = r.busquedaEuclidiana(idConsulta)

        String[][] a = consultas
        String[] b = ["ID", "Resultado", "Valor"]

        modeloResultados.setDataVector(a,b)
    }

    def buscarCoseno(Integer idConsulta) {
        Recuperador r = new Recuperador()
        def consultas = r.busquedaCoseno(idConsulta)

        String[][] a = consultas
        String[] b = ["ID", "Resultado", "Valor"]

        modeloResultados.setDataVector(a,b)
    }

    def buscarDice(Integer idConsulta) {
        Recuperador r = new Recuperador()
        def consultas = r.busquedaDice(idConsulta)

        String[][] a = consultas
        String[] b = ["ID", "Resultado", "Valor"]

        modeloResultados.setDataVector(a,b)
    }

    def buscarCosenoLSI(String k, Integer idConsulta) {
        Recuperador r = new Recuperador()
        def consultas = r.busquedaCosenoLSI(k, idConsulta)

        String[][] a = consultas
        String[] b = ["ID", "Resultado", "Valor"]

        modeloResultados.setDataVector(a,b)
    }

    def buscarManhattanLSI(String k, Integer idConsulta) {
        Recuperador r = new Recuperador()
        def consultas = r.busquedaManhattanLSI(k, idConsulta)

        String[][] a = consultas
        String[] b = ["ID", "Resultado", "Valor"]

        modeloResultados.setDataVector(a,b)
    }

     def buscarEuclidianaLSI(String k, Integer idConsulta) {
        Recuperador r = new Recuperador()
        def consultas = r.busquedaEuclidianaLSI(k, idConsulta)

        String[][] a = consultas
        String[] b = ["ID", "Resultado", "Valor"]

        modeloResultados.setDataVector(a,b)
    }

    def buscarProductoInternoLSI(String k, Integer idConsulta) {
        Recuperador r = new Recuperador()
        def consultas = r.busquedaProductoInternoLSI(k, idConsulta)

        String[][] a = consultas
        String[] b = ["ID", "Resultado", "Valor"]

        modeloResultados.setDataVector(a,b)
    }
    
}

