
package eu.toop.commander.dpsearch;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.toop.commander.dpsearch package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Resultlist_QNAME = new QName("", "resultlist");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.toop.commander.dpsearch
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResultListType }
     * 
     */
    public ResultListType createResultListType() {
        return new ResultListType();
    }

    /**
     * Create an instance of {@link EntityType }
     * 
     */
    public EntityType createEntityType() {
        return new EntityType();
    }

    /**
     * Create an instance of {@link ContactType }
     * 
     */
    public ContactType createContactType() {
        return new ContactType();
    }

    /**
     * Create an instance of {@link MatchType }
     * 
     */
    public MatchType createMatchType() {
        return new MatchType();
    }

    /**
     * Create an instance of {@link NameType }
     * 
     */
    public NameType createNameType() {
        return new NameType();
    }

    /**
     * Create an instance of {@link IDType }
     * 
     */
    public IDType createIDType() {
        return new IDType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "resultlist")
    public JAXBElement<ResultListType> createResultlist(ResultListType value) {
        return new JAXBElement<ResultListType>(_Resultlist_QNAME, ResultListType.class, null, value);
    }

}
