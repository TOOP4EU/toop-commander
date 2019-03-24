
package eu.toop.commander.dpsearch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MatchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MatchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="participantID" type="{}IDType"/>
 *         &lt;element name="docTypeID" type="{}IDType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="entity" type="{}EntityType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MatchType", propOrder = {
    "participantID",
    "docTypeID",
    "entity"
})
public class MatchType {

    @XmlElement(required = true)
    protected IDType participantID;
    protected List<IDType> docTypeID;
    @XmlElement(required = true)
    protected List<EntityType> entity;

    /**
     * Gets the value of the participantID property.
     * 
     * @return
     *     possible object is
     *     {@link IDType }
     *     
     */
    public IDType getParticipantID() {
        return participantID;
    }

    /**
     * Sets the value of the participantID property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDType }
     *     
     */
    public void setParticipantID(IDType value) {
        this.participantID = value;
    }

    /**
     * Gets the value of the docTypeID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the docTypeID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocTypeID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDType }
     * 
     * 
     */
    public List<IDType> getDocTypeID() {
        if (docTypeID == null) {
            docTypeID = new ArrayList<IDType>();
        }
        return this.docTypeID;
    }

    /**
     * Gets the value of the entity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityType }
     * 
     * 
     */
    public List<EntityType> getEntity() {
        if (entity == null) {
            entity = new ArrayList<EntityType>();
        }
        return this.entity;
    }

}
