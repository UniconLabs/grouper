/**
 * @author mchyzer
 * $Id: XmlExportGroup.java 6216 2010-01-10 04:52:30Z mchyzer $
 */
package edu.internet2.middleware.grouper.xml.export;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.Dom4JReader;

import edu.internet2.middleware.grouper.attr.assign.AttributeAssignValue;
import edu.internet2.middleware.grouper.hibernate.AuditControl;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibUtils;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateHandlerBean;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.misc.GrouperVersion;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouper.xml.importXml.XmlImportMain;


/**
 *
 */
public class XmlExportAttributeAssignValue {

  /**
   * 
   */
  private static final String XML_EXPORT_ATTRIBUTE_ASSIGN_VALUE_XPATH = "/grouperExport/attributeAssignValues/XmlExportAttributeAssignValue";

  /**
   * 
   */
  private static final String ATTRIBUTE_ASSIGN_VALUES_XPATH = "/grouperExport/attributeAssignValues";

  /** valueInteger */
  private Long valueInteger;
  
  
  
  /**
   * valueInteger
   * @return valueInteger
   */
  public Long getValueInteger() {
    return this.valueInteger;
  }

  /**
   * valueInteger
   * @param valueInteger1
   */
  public void setValueInteger(Long valueInteger1) {
    this.valueInteger = valueInteger1;
  }

  /**
   * valueString
   */
  private String valueString;
  
  /**
   * valueString
   * @return valueString
   */
  public String getValueString() {
    return this.valueString;
  }

  /**
   * valueString
   * @param valueString1
   */
  public void setValueString(String valueString1) {
    this.valueString = valueString1;
  }

  /** valueMemberId */
  private String valueMemberId;
  
  /**
   * valueMemberId
   * @return valueMemberId
   */
  public String getValueMemberId() {
    return this.valueMemberId;
  }

  /**
   * valueMemberId
   * @param valueMemberId1
   */
  public void setValueMemberId(String valueMemberId1) {
    this.valueMemberId = valueMemberId1;
  }

  /** attributeAssignId */
  private String attributeAssignId;

  /**
   * attributeAssignId
   * @return attributeAssignId
   */
  public String getAttributeAssignId() {
    return this.attributeAssignId;
  }

  /**
   * attributeAssignId1
   * @param attributeAssignId1
   */
  public void setAttributeAssignId(String attributeAssignId1) {
    this.attributeAssignId = attributeAssignId1;
  }

  /** uuid */
  private String uuid;
  
  /** createTime */
  private String createTime;

  /** modifierTime */
  private String modifierTime;

  /** hibernateVersionNumber */
  private long hibernateVersionNumber;

  /** contextId */
  private String contextId;

  /**
   * logger 
   */
  private static final Log LOG = GrouperUtil.getLog(XmlExportAttributeAssignValue.class);

  /**
   * 
   */
  public XmlExportAttributeAssignValue() {
    
  }

  /**
   * uuid
   * @return uuid
   */
  public String getUuid() {
    return this.uuid;
  }

  /**
   * uuid
   * @param uuid1
   */
  public void setUuid(String uuid1) {
    this.uuid = uuid1;
  }

  /**
   * createTime
   * @return createTime
   */
  public String getCreateTime() {
    return this.createTime;
  }

  /**
   * createTime
   * @param createTime1
   */
  public void setCreateTime(String createTime1) {
    this.createTime = createTime1;
  }

  /**
   * modifierTime
   * @return modifierTime
   */
  public String getModifierTime() {
    return this.modifierTime;
  }

  /**
   * modifierTime
   * @param modifierTime1
   */
  public void setModifierTime(String modifierTime1) {
    this.modifierTime = modifierTime1;
  }

  /**
   * hibernateVersionNumber
   * @return hibernateVersionNumber
   */
  public long getHibernateVersionNumber() {
    return this.hibernateVersionNumber;
  }

  /**
   * hibernateVersionNumber
   * @param hibernateVersionNumber1
   */
  public void setHibernateVersionNumber(long hibernateVersionNumber1) {
    this.hibernateVersionNumber = hibernateVersionNumber1;
  }

  /**
   * contextId
   * @return contextId
   */
  public String getContextId() {
    return this.contextId;
  }

  /**
   * contextId
   * @param contextId1
   */
  public void setContextId(String contextId1) {
    this.contextId = contextId1;
  }
  
  /**
   * convert to attributeDefName
   * @return the attributeDefName
   */
  public AttributeAssignValue toAttributeAssignValue() {
    AttributeAssignValue attributeAssignValue = new AttributeAssignValue();
    
    attributeAssignValue.setAttributeAssignId(this.attributeAssignId);
    attributeAssignValue.setContextId(this.contextId);
    attributeAssignValue.setCreatedOnDb(GrouperUtil.dateLongValue(this.createTime));
    attributeAssignValue.setHibernateVersionNumber(this.hibernateVersionNumber);
    attributeAssignValue.setLastUpdatedDb(GrouperUtil.dateLongValue(this.modifierTime));
    attributeAssignValue.setId(this.uuid);
    attributeAssignValue.setValueInteger(this.valueInteger);
    attributeAssignValue.setValueMemberId(this.valueMemberId);
    attributeAssignValue.setValueString(this.valueString);
    
    return attributeAssignValue;
  }

  /**
   * @param exportVersion
   * @return the xml string
   */
  public String toXml(GrouperVersion exportVersion) {
    StringWriter stringWriter = new StringWriter();
    this.toXml(exportVersion, stringWriter);
    return stringWriter.toString();
  }

  /**
   * @param exportVersion 
   * @param writer
   */
  public void toXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, Writer writer) {
    XStream xStream = XmlExportUtils.xstream();
  
    CompactWriter compactWriter = new CompactWriter(writer);
    
    xStream.marshal(this, compactWriter);
  
  }

  /**
   * parse the xml file for groups
   * @param xmlImportMain
   */
  public static void processXmlSecondPass(final XmlImportMain xmlImportMain) {
    xmlImportMain.getReader().addHandler( ATTRIBUTE_ASSIGN_VALUES_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
            }
            public void onEnd(ElementPath path) {
                // process a ROW element
                Element row = path.getCurrent();
  
                // prune the tree
                row.detach();
            }
        }
    );
  
    xmlImportMain.getReader().addHandler( XML_EXPORT_ATTRIBUTE_ASSIGN_VALUE_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
                // do nothing here...    
            }
            public void onEnd(ElementPath path) {
  
              Element row = null;
              try {
                // process a ROW element
                row = path.getCurrent();
  
                // prune the tree
                row.detach();
  
                XmlExportAttributeAssignValue xmlExportAttributeAssignValueFromFile = (XmlExportAttributeAssignValue)xmlImportMain.getXstream().unmarshal(new Dom4JReader(row));
                
                AttributeAssignValue attributeAssignValue = xmlExportAttributeAssignValueFromFile.toAttributeAssignValue();
                
                XmlExportUtils.syncImportableMultiple(attributeAssignValue, xmlImportMain);
                
                xmlImportMain.incrementCurrentCount();
              } catch (RuntimeException re) {
                LOG.error("Problem importing attributeAssignValue: " + XmlExportUtils.toString(row), re);
                throw re;
              }
            }
        }
    );
  
  }

  /**
   * get db count
   * @return db count
   */
  public static long dbCount() {
    long result = HibernateSession.byHqlStatic().createQuery("select count(*) from AttributeAssignValue").uniqueResult(Long.class);
    return result;
  }
  

  /**
   * 
   * @param writer
   * @param xmlExportMain
   */
  public static void exportAttributeAssignValues(final Writer writer, final XmlExportMain xmlExportMain) {
    //get the members
    HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_OR_USE_EXISTING, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
      
      public Object callback(HibernateHandlerBean hibernateHandlerBean)
          throws GrouperDAOException {
  
        Session session = hibernateHandlerBean.getHibernateSession().getSession();
  
        //select all attribute assign value in order
        Query query = session.createQuery(
            "select theAttributeAssignValue from AttributeAssignValue as theAttributeAssignValue order by theAttributeAssignValue.id");
  
        GrouperVersion grouperVersion = new GrouperVersion(GrouperVersion.GROUPER_VERSION);
        try {
          writer.write("  <attributeAssignValues>\n");
  
          //this is an efficient low-memory way to iterate through a resultset
          ScrollableResults results = null;
          try {
            results = query.scroll();
            while(results.next()) {
              Object object = results.get(0);
              final AttributeAssignValue attributeAssignValue = (AttributeAssignValue)object;
              
              //comments to dereference the foreign keys
              if (xmlExportMain.isIncludeComments()) {
                HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_NEW, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
                  
                  public Object callback(HibernateHandlerBean hibernateHandlerBean)
                      throws GrouperDAOException {
                    try {
                      writer.write("\n    <!-- value: ");
                      
                      if (attributeAssignValue.getValueInteger() != null) {
                        writer.write(attributeAssignValue.getValueInteger().toString());
                      } else if (!StringUtils.isBlank(attributeAssignValue.getValueMemberId())) {
                        XmlExportUtils.toStringMember("value", writer, attributeAssignValue.getValueMemberId(), false);
                      } else if (attributeAssignValue.getValueString() != null) {
                        writer.write(attributeAssignValue.getValueString());
                      } else {
                        writer.write("null");
                      }
                      writer.write(", ");
                      
                      XmlExportUtils.toStringAttributeAssign(null, writer, attributeAssignValue.getAttributeAssignId(), false);

                      writer.write(" -->\n");
                      return null;
                    } catch (IOException ioe) {
                      throw new RuntimeException(ioe);
                    }
                  }
                });
              }
              
              XmlExportAttributeAssignValue xmlExportAttributeDefName = attributeAssignValue.xmlToExportAttributeAssignValue(grouperVersion);
              writer.write("    ");
              xmlExportAttributeDefName.toXml(grouperVersion, writer);
              writer.write("\n");
            }
          } finally {
            HibUtils.closeQuietly(results);
          }
          
          if (xmlExportMain.isIncludeComments()) {
            writer.write("\n");
          }
          
          //end the members element 
          writer.write("  </attributeAssignValues>\n");
        } catch (IOException ioe) {
          throw new RuntimeException("Problem with streaming attribute assign values", ioe);
        }
        return null;
      }
    });
  }

  /**
   * take a reader (e.g. dom reader) and convert to an xml export group
   * @param exportVersion
   * @param hierarchicalStreamReader
   * @return the bean
   */
  public static XmlExportAttributeAssignValue fromXml(@SuppressWarnings("unused") GrouperVersion exportVersion, 
      HierarchicalStreamReader hierarchicalStreamReader) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeAssignValue xmlExportAttributeAssignValue = (XmlExportAttributeAssignValue)xStream.unmarshal(hierarchicalStreamReader);
  
    return xmlExportAttributeAssignValue;
  }

  /**
   * 
   * @param exportVersion
   * @param xml
   * @return the object from xml
   */
  public static XmlExportAttributeAssignValue fromXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, String xml) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeAssignValue xmlExportAttributeAssignValue = (XmlExportAttributeAssignValue)xStream.fromXML(xml);
  
    return xmlExportAttributeAssignValue;
  }

  /**
   * parse the xml file for attributeAssignValues
   * @param xmlImportMain
   */
  public static void processXmlFirstPass(final XmlImportMain xmlImportMain) {
    xmlImportMain.getReader().addHandler( ATTRIBUTE_ASSIGN_VALUES_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
            }
            public void onEnd(ElementPath path) {
                // process a ROW element
                Element row = path.getCurrent();

                // prune the tree
                row.detach();
            }
        }
    );

    xmlImportMain.getReader().addHandler( XML_EXPORT_ATTRIBUTE_ASSIGN_VALUE_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
                // do nothing here...    
            }
            public void onEnd(ElementPath path) {
                // process a ROW element
                Element row = path.getCurrent();

                // prune the tree
                row.detach();

                xmlImportMain.incrementTotalImportFileCount();
            }
        }
    );
 
  }

}
