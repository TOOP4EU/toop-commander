
package eu.toop.commander.dpsearch;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ResultListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultListType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="match" type="{}MatchType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="total-result-count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="used-result-count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="result-page-index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="result-page-count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="first-result-index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="last-result-index" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="query-terms" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="creation-dt" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultListType", propOrder = {
    "match"
})
@XmlRootElement(name="resultlist")
public class ResultListType {

    protected List<MatchType> match;
    @XmlAttribute(name = "version", required = true)
    protected String version;
    @XmlAttribute(name = "total-result-count", required = true)
    protected int totalResultCount;
    @XmlAttribute(name = "used-result-count", required = true)
    protected int usedResultCount;
    @XmlAttribute(name = "result-page-index", required = true)
    protected int resultPageIndex;
    @XmlAttribute(name = "result-page-count", required = true)
    protected int resultPageCount;
    @XmlAttribute(name = "first-result-index", required = true)
    protected int firstResultIndex;
    @XmlAttribute(name = "last-result-index", required = true)
    protected int lastResultIndex;
    @XmlAttribute(name = "query-terms", required = true)
    protected String queryTerms;
    @XmlAttribute(name = "creation-dt", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDt;

    /**
     * Gets the value of the match property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the match property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMatch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MatchType }
     * 
     * 
     */
    public List<MatchType> getMatch() {
        if (match == null) {
            match = new ArrayList<MatchType>();
        }
        return this.match;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the totalResultCount property.
     * 
     */
    public int getTotalResultCount() {
        return totalResultCount;
    }

    /**
     * Sets the value of the totalResultCount property.
     * 
     */
    public void setTotalResultCount(int value) {
        this.totalResultCount = value;
    }

    /**
     * Gets the value of the usedResultCount property.
     * 
     */
    public int getUsedResultCount() {
        return usedResultCount;
    }

    /**
     * Sets the value of the usedResultCount property.
     * 
     */
    public void setUsedResultCount(int value) {
        this.usedResultCount = value;
    }

    /**
     * Gets the value of the resultPageIndex property.
     * 
     */
    public int getResultPageIndex() {
        return resultPageIndex;
    }

    /**
     * Sets the value of the resultPageIndex property.
     * 
     */
    public void setResultPageIndex(int value) {
        this.resultPageIndex = value;
    }

    /**
     * Gets the value of the resultPageCount property.
     * 
     */
    public int getResultPageCount() {
        return resultPageCount;
    }

    /**
     * Sets the value of the resultPageCount property.
     * 
     */
    public void setResultPageCount(int value) {
        this.resultPageCount = value;
    }

    /**
     * Gets the value of the firstResultIndex property.
     * 
     */
    public int getFirstResultIndex() {
        return firstResultIndex;
    }

    /**
     * Sets the value of the firstResultIndex property.
     * 
     */
    public void setFirstResultIndex(int value) {
        this.firstResultIndex = value;
    }

    /**
     * Gets the value of the lastResultIndex property.
     * 
     */
    public int getLastResultIndex() {
        return lastResultIndex;
    }

    /**
     * Sets the value of the lastResultIndex property.
     * 
     */
    public void setLastResultIndex(int value) {
        this.lastResultIndex = value;
    }

    /**
     * Gets the value of the queryTerms property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryTerms() {
        return queryTerms;
    }

    /**
     * Sets the value of the queryTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryTerms(String value) {
        this.queryTerms = value;
    }

    /**
     * Gets the value of the creationDt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDt() {
        return creationDt;
    }

    /**
     * Sets the value of the creationDt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDt(XMLGregorianCalendar value) {
        this.creationDt = value;
    }

}
