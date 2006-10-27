/*
$Header: /home/hagleyj/i2mi/signet/src/edu/internet2/middleware/signet/subjsrc/PersistedSignetSource.java,v 1.2 2006-10-27 21:46:35 ddonn Exp $

Copyright (c) 2006 Internet2, Stanford University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

	@author ddonn
*/
package edu.internet2.middleware.signet.subjsrc;

import java.util.Calendar;
import java.util.Vector;
import edu.internet2.middleware.signet.ObjectNotFoundException;
import edu.internet2.middleware.signet.dbpersist.HibernateDB;
import edu.internet2.middleware.subject.provider.SourceManager;
import net.sf.hibernate.Query;


/**
 * This class represents a SignetSource for the persisted (e.g. Hibernate) Subjects.
 * It is designed to be created when the SubjectSources.xml (Signet's subject-
 * source configuration file) is parsed and the <persistedSubjectSource> tag is found.
 * This allows Signet to treat the persisted store as just another
 * SubjectAPI-like Source.
 */
public class PersistedSignetSource extends SignetSource
{
	public static final String	TYPE_PERSISTED_SRC = "persistedSrcType";

	// conversion factor for minutes to/from milliseconds
	protected static final long minutesToMillis = (60 * 1000);

	/** The Persisted Store Manager */
	protected HibernateDB		persistMgr;

	/**
	 * Elapsed time to wait before refreshing Persistent store from the SourceAPI.
	 * Value is specified in SubjectSources.xml using the 'latencyMinutes' attribute
	 * of the <persistedSubjectSource> tag.
	 * Default: 60 minutes, stored as milliseconds.
	 */
	protected long				latency;

	////////////////////////////////////
	// The following are "attributes of interest" for Signet (when Signet acts
	// like an application, in it's GUI for example). Their values
	// correspond to mappedAttribute names in each Source brought in from the
	// SubjectAPI via the SubjectSources.xml.
	////////////////////////////////////
	protected String			signetName;
	protected String			signetSortName;
	protected String			signetDescription;
	protected String			signetDisplayId;
	protected String			contactEmail;
	protected Vector			outputXml;
	protected Vector			uniqueId;


	/**
	 * default constructor
	 * Support for Digester
	 */
	public PersistedSignetSource()
	{
		// initialize super's attributes (Note: super() is called implicitly)
		type = TYPE_PERSISTED_SRC;
		status = STATUS_ACTIVE;
		failover = true;
		usage.add(USAGE_DEFAULT);

		// initialize class-specific attributes
		latency = 60 * minutesToMillis;
		signetName = null;
		signetSortName = null;
		signetDescription = null;
		signetDisplayId = null;
		contactEmail = null;
		outputXml = new Vector();
		uniqueId = new Vector();

		persistMgr = null;
	}


	public void setPersistedStoreMgr(HibernateDB persistMgr)
	{
		this.persistMgr = persistMgr;
	}

	public HibernateDB getPersistedStoreMgr()
	{
		return (persistMgr);
	}


	public void setSignetSources(SignetSources signetSources)
	{
		this.signetSources = signetSources;
	}


	/**
	 * Gets the amount of elapsed time of refreshs between SourceAPI and Persistent store
	 * Supports Digetster and SubjectSources.xml parsing
	 * @return Returns the latency in minutes.
	 */
	public long getLatencyMinutes()
	{
		return (latency / minutesToMillis);
	}

	/**
	 * Gets the amount of elapsed time of refreshs between SourceAPI and Persistent store
	 * Supports Digetster and SubjectSources.xml parsing
	 * @return Returns the latency in milliseconds.
	 */
	public long getLatencyMillis()
	{
		return (latency);
	}

	/**
	 * Sets the amount of elapsed time of refreshs between SourceAPI and Persistent store.
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param latencyMinutes The latencyMinutes to set, converted to millis internally
	 */
	public void setLatencyMinutes(String latencyMinutes)
	{
		latency = Long.parseLong(latencyMinutes) * minutesToMillis;
	}

	/**
	 * Determines if this Subject may need re-synchronization with it's Source
	 * @param signetSubject The Subject in question
	 * @return true if Subject is persisted and older than 'latency', otherwise false
	 */
	public boolean isStale(SignetSubject signetSubject)
	{
		if ((null == signetSubject) || !signetSubject.isPersisted())
			return (false);

		long now = Calendar.getInstance().getTimeInMillis();
		long syncTime = signetSubject.getSynchDatetime().getTime();
		boolean retval = latency < (now - syncTime);

		return (retval);
	}


	/**
	 * Set the signetName
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param name The Signet name to set
	 */
	public void setSignetName(String name)
	{
		signetName = name;
	}

	/**
	 * Get the Signet name
	 * Supports Digetster and SubjectSources.xml parsing
	 * @return The Signet name
	 */
	public String getSignetName()
	{
		return (signetName);
	}

	/**
	 * Sets the Signet Sort Name
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param sortName
	 */
	public void setSignetSortName(String sortName)
	{
		signetSortName = sortName;
	}

	/**
	 * Supports Digetster and SubjectSources.xml parsing
	 * @return The Signet Sort Name
	 */
	public String getSignetSortName()
	{
		return (signetSortName);
	}


	/**
	 * Set the Signet Description
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param signetDesc
	 */
	public void setSignetDescription(String signetDesc)
	{
		signetDescription = signetDesc;
	}

	/**
	 * Supports Digetster and SubjectSources.xml parsing
	 * @return The Signet Description
	 */
	public String getSignetDescription()
	{
		return (signetDescription);
	}


	/**
	 * Sets the SignetDisplayId
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param displayId
	 */
	public void setSignetDisplayId(String displayId)
	{
		signetDisplayId = displayId;
	}

	/**
	 * Supports Digetster and SubjectSources.xml parsing
	 * @return The Signet Display Id
	 */
	public String getSignetDisplayId()
	{
		return (signetDisplayId);
	}

	
	/**
	 * Define the field used by Signet as the email contact
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param email The email contact
	 */
	public void setContactEmail(String email)
	{
		contactEmail = email;
	}

	/**
	 * @return The email contact value
	 */
	public String getContactEmail()
	{
		return (contactEmail);
	}


	/**
	 * Adds OutputXml field names to this Source. OutputXml tags may contain
	 * either single entries or multiple, comma-separated entries.
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param outputXmlStr
	 */
	public void addOutputXml(String outputXmlStr)
	{
		addValueListToVector(outputXmlStr, outputXml);
	}

	/**
	 * @return The entire list (Vector) of outputXml
	 */
	public Vector getOutputXml()
	{
		return (outputXml);
	}

	/**
	 * Tests to see if outputXmlStr is in the list (Vector) of outputXml values
	 * @param outputXmlStr
	 * @return True if found, false otherwise
	 */
	public boolean hasOutputXmlValue(String outputXmlStr)
	{
		return (outputXml.contains(outputXmlStr));
	}


	/**
	 * Adds UniqueId field names to this Source. UniqueId tags may contain
	 * either single entries or multiple, comma-separated entries. Collectively,
	 * UniqueId values define the fields used as a primary key
	 * Supports Digetster and SubjectSources.xml parsing
	 * @param uniqueIdStr
	 */
	public void addUniqueId(String uniqueIdStr)
	{
		addValueListToVector(uniqueIdStr, uniqueId);
	}

	/**
	 * @return The entire list (Vector) of uniqueId
	 */
	public Vector getUniqueId()
	{
		return (uniqueId);
	}

	/**
	 * Tests to see if uniqueIdStr is in the list (Vector) of uniqueId values
	 * @param uniqueIdStr
	 * @return True if found, false otherwise
	 */
	public boolean hasUniqueIdValue(String uniqueIdStr)
	{
		return (uniqueId.contains(uniqueIdStr));
	}


	////////////////////////////////////////
	// support for persistent store (i.e. Hibernate)
	////////////////////////////////////////

	public Query createQuery()
	{
		return null;
	}

	public void beginTransaction()
	{
	}

	public void close()
	{
	}

	public void commit()
	{
	}

	public void save()
	{
	}


	///////////////////////////////////
	// overrides SignetSource
	///////////////////////////////////

	/**
	 * Override SignetSource.setSourceManager() because there is no corresponding
	 * SubjectAPI Source lookup to perform for a PersistedSignetSource. Each
	 * Subject retrieved from Persisted store has it's own SourceId reference!
	 * @see edu.internet2.middleware.signet.subjsrc.SignetSource#setSourceManager(edu.internet2.middleware.subject.provider.SourceManager)
	 */
	public void setSourceManager(SourceManager sourceManager)
	{
		this.sourceManager = sourceManager;
	}


	/**
	 * Add a usage to this source. Called by Digester when parsing SubjectSources.xml.
	 * Supports Digester and SubjectSources.xml parsing
	 */
	public void addUsage(String usageStr)
	{
		; // do nothing
	}

	/**
	 * Tests to see if this SignetSource supports the given usage (as defined in SubjectSources.xml)
	 * @param usage The usage to test for
	 * @return True if usage is a match, false otherwise.
	 */
	public boolean hasUsage(String usage)
	{
		return (false);
	}

	/**
	 * @return Returns the Vector of all usage values
	 */
	public Vector getUsage()
	{
		return (new Vector());
	}


	/**
	 * Supports Digester and SubjectSources.xml parsing
	 * @param status
	 */
	public void setStatus(String status)
	{
		; // do nothing
	}


	/**
	 */
	public void setSubjectType(String type)
	{
		; // do nothing
	}


	/**
	 * Find a SignetSubject from the Persisted store that matches the given
	 * sourceId and subjectId. If found, check if subject.isStale() and 
	 * try to get fresh Subject info from SubjectAPI.
	 * Pseudo-override of SignetSubject#getSubject(String subjectId)
	 * @param sourceId The sourceId
	 * @param subjectId The subjectId
	 * @return A SignetSubject if found, otherwise null
	 */
	public SignetSubject getSubject(String sourceId, String subjectId)
	{
		SignetSubject retval = null;

		try
		{
			retval = persistMgr.getSubject(sourceId, subjectId);
			if (null != retval)
				retval.setSource(signetSources.getSource(sourceId));
			if (isStale(retval))
				signetSources.synchSubject(retval);
		}
		catch (ObjectNotFoundException e)
		{
			log.warn(e);
		}

		return (retval);
	}


	/**
	 * Returns a Vector of SignetSubject objects from the Persisted store.
	 * @return A Vector of SignetSubject objects, or empty Vector (not null!)
	 */
	public Vector getSubjects()
	{
		return null;
	}


	///////////////////////////////////
	// implements Source
	///////////////////////////////////

//	public void init()
//	{
//	}

	////////////////////////////////
	// overrides Object
	////////////////////////////////

	public String toString()
	{
		return ("PersistedSignetSource: Id=\"" + getId() + "\" " +
				"Name=\"" + getName() + "\" " +
				"Description=\"" + getSignetDescription() + "\" " +
				"RefreshLatency=" + (latency / minutesToMillis) + " " +
				"SortName=\"" + getSignetSortName() + "\" " +
				"DisplayId=\"" + getSignetDisplayId() + "\" " +
				"ContactEmail=\"" + getContactEmail() + "\" " +
				"\n" +
				vectorToString("UniqueIds", uniqueId) + "\n" +
				vectorToString("OutputXml", outputXml));
	}


}
