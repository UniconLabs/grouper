/*
$Header: /home/hagleyj/i2mi/signet/src/edu/internet2/middleware/signet/subjsrc/SignetSources.java,v 1.2 2006-10-27 21:46:35 ddonn Exp $

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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import edu.internet2.middleware.signet.Signet;
import edu.internet2.middleware.signet.SignetRuntimeException;
import edu.internet2.middleware.signet.dbpersist.HibernateDB;
import edu.internet2.middleware.signet.resource.ResLoaderApp;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.provider.SourceManager;

/**
 * SignetSources is a delegator of SignetSource tasks. It holds references to
 * three significant types of SignetSources:
 *   1) the Signet Application Source - this SignetSource is always present and
 *      acts as a container for the Signet "super subject"; the Subject that
 *      represents Signet itself.
 *   2) the Persistent Store Source - this SignetSource allows the application to
 *      treat Signet's persistent store as a Subject source. Subjects that have
 *      been previously persisted will be available from this Source.
 *   3) All "ordinary" Sources declared in the SignetSources.xml file
 */
public class SignetSources
{
//TODO Load these as resources!!
	/* Tags for Digester parser */
	public static final String		TAG_SEP					= "/";
	public static final String		SIGNET_SRCS_TAG			= "signetSubjectSources";
	public static final String		SIGNET_SRC_TAG			= "signetSubjectSource";
	public static final String		PERSISTED_SRC_TAG		= "persistedSubjectSource";
	public static final String		SIGNET_NAME_TAG			= "signetName";
	public static final String		SIGNET_SORTNAME_TAG		= "signetSortName";
	public static final String		SIGNET_DESC_TAG			= "signetDescription";
	public static final String		SIGNET_DISPLAYID_TAG	= "signetDisplayId";
	public static final String		SIGNET_OUTPUTXML_TAG	= "outputXml";
	public static final String		SIGNET_UNIQUEID_TAG		= "uniqueId";
	public static final String		SIGNET_EMAIL_TAG		= "contactEmail";
	public static final String		SIGNET_USAGE_TAG		= "usage";
	public static final String		SIGNET_MAPPEDATTR_TAG	= "mappedAttribute";

	// logging
	protected Log				log = LogFactory.getLog(Signet.class);

	/** List of "standard" Subject Sources as defined in SubjectSources.xml.
	 * signetSources is a Vector, as opposed to a Hashtable, because it will be
	 * a relatively short list and lookup times shouldn't be an issue.
	 * This Vector gets populated by parsing the SubjectSources.xml via Digester.
	 */
	protected Vector			signetSources;

	/** The one Source that is the Persisted store of Subjects. Signet is told of
	 * the Persisted store from the parsing of the SubjectSources.xml when it
	 * hits a <persistedSubjectSource> tag.
	 */
	protected PersistedSignetSource	persistedSource;

	/** The Source for the Signet Application Subject (i.e. super Subject). This
	 * is built-in to Signet and is independent of SubjectSources.xml or any
	 * other Subject Sources.
	 */
	protected SignetAppSource	sigAppSource;

	/** The SourceAPI SourceManager instance */
	protected SourceManager		sourceManager;

	/** Local copy of Signet, for use by PersistedSignetSource */
	protected Signet			signet;


	/** default constructor */
	private SignetSources()
	{
		persistedSource = null;
		signetSources = new Vector();

		try { sourceManager = SourceManager.getInstance(); }
		catch (Exception e)
		{
			throw new SignetRuntimeException(ResLoaderApp.getString("Signet.msg.exc.srcMgr"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Constructor
	 * @param configFile An XML file containing the Signet-to-Source attribute mappings
	 */
	public SignetSources(String configFile, Signet signetInstance)
	{
		this();

		if ((null != configFile) && (0 < configFile.length()))
		{
			signet = signetInstance;
			if (parseConfigFile(configFile))
			{
				log.debug("SignetSubjectSources.SignetSubjectSources:\n  " + this);
			}
			else
				log.error("SignetSubjectSources.SignetSubjectSources: problems parsing Subject-Source file \"" +
						configFile + "\"");
		}

		sigAppSource = new SignetAppSource(this, SignetAppSource.SIGNET_SOURCE_ID);
	}


	////////////////////////////////////////
	// Configuration Parsing Stuff
	////////////////////////////////////////

	/**
	 * Parse the configuration file
	 * @param configFile
	 * @return true on success, false otherwise
	 */
	protected synchronized boolean parseConfigFile(String configFile)
	{
		// mapping for Digester and SignetSubjects.xml
// This doesn't seem to work reliably
//		String[] classPropertiesMap =
//			{"id",			"name",			/*"latency",*/			"subjectType",
//				"status",		"failover"};
//		// mapping for Digester and SignetSubjects.xml
//		String[] xmlAttributesMap =
//			{"sourceId",	"sourceName",	/*"latencyMinutes",*/	"subjectType",
//				"status",		"failover"};

		boolean status = false; // assume failure

		Digester digester = new Digester();
		digester.setValidating(false);
		digester.push(this);

		String tag = buildTag(SIGNET_SRCS_TAG) + PERSISTED_SRC_TAG;
		digester.addObjectCreate(tag, PersistedSignetSource.class);
		digester.addSetProperties(tag);
//		digester.addSetProperties(tag, xmlAttributesMap, classPropertiesMap);
		digester.addSetNext(tag, "addSource");

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(PERSISTED_SRC_TAG) + SIGNET_NAME_TAG;
		digester.addCallMethod(tag, "setName", 1);
		digester.addCallParam(tag, 0);

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(PERSISTED_SRC_TAG) + SIGNET_SORTNAME_TAG;
		digester.addCallMethod(tag, "setSignetSortName", 1);
		digester.addCallParam(tag, 0);

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(PERSISTED_SRC_TAG) + SIGNET_DESC_TAG;
		digester.addCallMethod(tag, "setSignetDescription", 1);
		digester.addCallParam(tag, 0);

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(PERSISTED_SRC_TAG) + SIGNET_DISPLAYID_TAG;
		digester.addCallMethod(tag, "setSignetDisplayId", 1);
		digester.addCallParam(tag, 0);

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(PERSISTED_SRC_TAG) + SIGNET_OUTPUTXML_TAG;
		digester.addCallMethod(tag, "addOutputXml", 1);
		digester.addCallParam(tag, 0);

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(PERSISTED_SRC_TAG) + SIGNET_UNIQUEID_TAG;
		digester.addCallMethod(tag, "addUniqueId", 1);
		digester.addCallParam(tag, 0);

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(PERSISTED_SRC_TAG) + SIGNET_EMAIL_TAG;
		digester.addCallMethod(tag, "setContactEmail", 1);
		digester.addCallParam(tag, 0);


		tag = buildTag(SIGNET_SRCS_TAG) + SIGNET_SRC_TAG;
		digester.addObjectCreate(tag, SignetSource.class);
		digester.addSetProperties(tag);
//		digester.addSetProperties(tag, xmlAttributesMap, classPropertiesMap);
		digester.addSetNext(tag, "addSource");

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(SIGNET_SRC_TAG) + SIGNET_USAGE_TAG;
		digester.addCallMethod(tag, "addUsage", 1);
		digester.addCallParam(tag, 0);

		tag = buildTag(SIGNET_SRCS_TAG) + buildTag(SIGNET_SRC_TAG) + SIGNET_MAPPEDATTR_TAG;
		digester.addCallMethod(tag, "addMappedAttribute", 2);
		digester.addCallParam(tag, 0, "mappedName");
		digester.addCallParam(tag, 1, "sourceName");

		try
		{
			InputStream inStream = getClass().getResourceAsStream(configFile);
			digester.parse(inStream);
			status = true;
		}
		catch (IOException e)
		{
			log.error(e);
		}
		catch (SAXException e)
		{
			log.error(e);
		}
		finally
		{
			digester = null;
		}

		return (status);
	}


	protected String buildTag(String tagVal)
	{
		return (tagVal + TAG_SEP);
	}


	/**
	 * Add a SignetSource, parsed from the Subject Source config file, to
	 * this SignetSources. If a subject source with the same ID already
	 * exists, the 'add' is not performed.
	 * Support for Digester
	 * @param signetSource The SignetSource to add.
	 */
	public synchronized void addSource(SignetSource signetSource)
	{
		if (null == signetSource)
			return;

		String srcId = signetSource.getId();
		if ((null != srcId) && (0 < srcId.length()))
		{
System.out.println("SignetSources.addSource: about to add SignetSource:\n" + signetSource.toString());
			// check if it's already in the "normal" source list
			if (null == getSource(srcId))
			{
				if (signetSource instanceof PersistedSignetSource)
				{
					if (null == persistedSource)
					{
						persistedSource = (PersistedSignetSource)signetSource;
						persistedSource.setSignetSources(this);
						persistedSource.setPersistedStoreMgr(new HibernateDB(signet));
					}
					else
						log.error("A " + PERSISTED_SRC_TAG + " already exists. Ignoring new " + PERSISTED_SRC_TAG + " named " + signetSource.getName());
				}
				else
				{
					signetSource.setSourceManager(sourceManager); // add the SourceManager and lookup the SubjectAPI Source
					signetSources.add(signetSource); // append to end of list
				}

				signetSource.setParent(this);
			}
			else
				log.info("A " + SIGNET_SRC_TAG + " with ID=\"" + srcId + "\" already exists. Ignoring.");
		}
		else
			log.info("Null or empty SourceId while attempting to add the following SignetSource:\n" + signetSource.toString());
	}

	/**
	 * Remove a signetSubjectSource from our list of Sources
	 * @return The SignetSource that was removed if it existed in the list, otherwise null
	 */
	public SignetSource removeSource(String sourceId)
	{
		SignetSource retval = null;
		if ((null != sourceId) && (0 < sourceId.length()))
		{
			if (null != (retval = getSource(sourceId)))
				signetSources.remove(retval);
		}

		return (retval);
	}

	////////////////////////////////////
	// Source & Subject management API
	////////////////////////////////////

	/**
	 * Get a Subject that matches the given subjectId. First, attempt to find the
	 * requested subject from the PersistedSignetSource. Next, see if it is the
	 * Signet "super" subject Source. Finally, try finding it in the SubjectAPI.
	 * Facade method for SignetSource.
	 * @param subjectId The subject ID to match
	 * @return The matching SignetSubject or null
	 */
	public SignetSubject getSubject(String sourceId, String subjectId)
	{
		SignetSubject retval = null;

		// first, check for persisted subject
		if (null != persistedSource)
			retval = persistedSource.getSubject(sourceId, subjectId);

//		// next, if it's the Signet "super" subject and source (i.e. Signet Application)
//		if ((null == retval) &&
//				(sourceId.equals(SignetAppSource.SIGNET_SOURCE_ID)) &&
//				(subjectId.equals(SignetSubject.SIGNET_SUBJECT_ID)))
//			retval = (SignetSubject)sigAppSource.getSubject(subjectId);
//
		// if it's not persisted check for a real source/subject from SubjectAPI
		if (null == retval)
			retval = getSubjectBySource(sourceId, subjectId);

		return (retval);
	}

	/**
	 * Get a Subject that matches the given identifier. This method scans all
	 * known SignetSource objects, in order, until a match is found.
	 * Facade method for SignetSource.
	 * @param identifier A String that is compared to various fields/attributes (e.g. sunetId)
	 * @return A matching SignetSubject or null
	 */
	public SignetSubject getSubjectByIdentifier(String identifier)
	{
		if ((null == identifier) || (0 >= identifier.length()))
			return (null);

		SignetSubject retval = null;

		for (Iterator srcs = signetSources.iterator();
				srcs.hasNext() && (null == retval);)
		{
			SignetSource src = (SignetSource)srcs.next();
			retval = (SignetSubject)src.getSubjectByIdentifier(identifier);
		}

		return (retval);
	}

	/**
	 * Get all Subjects that match the given identifier. This method scans all
	 * known SignetSource objects, in order, and collects all matching Subjects
	 * for all Sources.
	 * @param identifier A String that is compared to various fields/attributes (e.g. sunetId)
	 * @return A Set of matching SignetSubjects, or an empty Set (not null!)
	 */
	public Set getSubjectsByIdentifier(String identifier)
	{
		HashSet retval = new HashSet();

		if ((null == identifier) || (0 >= identifier.length()))
			return (retval);

		for (Iterator srcs = signetSources.iterator(); srcs.hasNext(); )
		{
			SignetSource src = (SignetSource)srcs.next();
			Subject subj = src.getSubjectByIdentifier(identifier);
			if (null != subj)
				retval.add(subj);
		}

		return (retval);
	}

	/**
	 * Get a Subject that matches the given identifier. This method scans the
	 * SignetSource matching sourceId, if found, for the given subjectId.
	 * Facade method for @see SignetSource#getSubject() .
	 * @param sourceId A String representing the Source to use.
	 * @param subjectId The subject ID to match
	 * @return A matching SignetSubject or null
	 */
	public SignetSubject getSubjectBySource(String sourceId, String subjectId)
	{
		if ((0 >= sourceId.length()) || (0 >= subjectId.length()))
			return (null);

		SignetSubject retval = null;
		SignetSource src = getSource(sourceId);
		if (null != src)
			retval = (SignetSubject)src.getSubject(subjectId);

		return (retval);
	}

	/**
	 * Returns a Vector of all SignetSubject objects for all known SignetSource objects.
	 * @return A Vector of all SignetSubject objects, or an empty Vector (not null!).
	 */
	public Vector getSubjects()
	{
		Vector retval = new Vector();

		for (Iterator srcs = signetSources.iterator(); srcs.hasNext();)
		{
			SignetSource src = (SignetSource)srcs.next();
			retval.addAll(src.getSubjects());
		}

		return (retval);
	}

	/**
	 * Get all Subjects for a given Source. This method scans the SignetSource
	 * matching sourceId, if found.
	 * @param sourceId A String representing the Source to use.
	 * @return A Vector of SignetSubjects, or empty Vector (not null!)
	 */
	public Vector getSubjectsBySource(String sourceId)
	{
		Vector retval = new Vector();

		SignetSource src = getSource(sourceId);
		if (null != src)
			retval = src.getSubjects();

		return (retval);
	}

	/**
	 * Get all Subjects that match the given usage. This method scans all known
	 * SignetSource objects that are members of the given usage.
	 * Facade method for SignetSource.
	 * @param usage A String representing the Source usage attribute.
	 * @return A Vector of SignetSubjects, or empty Vector (not null!)
	 */
	public Vector getSubjectsByUsage(String usage)
	{
		Vector retval = new Vector();

		for (Iterator srcs = getSourcesByUsage(usage).iterator(); srcs.hasNext();)
		{
			SignetSource src = (SignetSource)srcs.next();
			retval.addAll(src.getSubjects());
		}

		return (retval);
	}

	/**
	 * Get all Subjects that match the given type. This method scans all known SignetSource objects that are
	 * members of the given type. Facade method for SignetSource.
	 * @param type A String representing the Source type attribute.
	 * @return A Vector of SignetSubjects, or empty Vector (not null!)
	 */
	public Vector getSubjectsByType(String type)
	{
		Vector retval = new Vector();

		for (Iterator srcs = getSourcesByType(type).iterator(); srcs.hasNext();)
		{
			SignetSource src = (SignetSource)srcs.next();
			retval.addAll(src.getSubjects());
		}

		return (retval);
	}


	/**
	 * Get a Subject that matches the given type. This method scans all known
	 * SignetSource objects that are of the given type.
	 * @param type A String representing the Source type attribute.
	 * @param subjectId The ID of the Subject
	 * @return The first SignetSubject that matches type and id, or null if no
	 * Subject is found
	 */
	public SignetSubject getSubjectByType(String type, String subjectId)
	{
		SignetSubject retval = null;

		for (Iterator srcs = getSourcesByType(type).iterator();
				srcs.hasNext() && (null == retval);)
		{
			SignetSource src = (SignetSource)srcs.next();
			retval = (SignetSubject)src.getSubject(subjectId);
		}

		return (retval);
	}


	/**
	 * Attempt to refresh this SignetSubject with information from it's original
	 * Source (in the SubjectAPI). Typically, this method is called for Subjects
	 * that have been retrieved from Persisted store and have exceeded their
	 * latency (i.e. isStale() == true).
	 * @return true if the synchronization occurred (whether or not new
	 * information was actually obtained) or false if the Source is unavailable
	 */
	public boolean synchSubject(SignetSubject sigSubject)
	{
		if (null == sigSubject)
			return (false);

		SignetSource sigSource = getSource(sigSubject.getSourceId());
		if (null == sigSource)
			return (false);

		Subject apiSubject = sigSource.getSubject(sigSubject.getId());
		if (null == apiSubject)
			return (false);

		return (sigSubject.synchSubject(apiSubject));
	}


	/**
	 * Returns a Vector of all known SignetSource objects.
	 * @return A Vector of known SignetSource objects, or an empty Vector (not null!).
	 */
	public Vector getSources()
	{
		return (signetSources);
	}

	/**
	 * Returns a Vector of all SignetSource objects that match the given usage.
	 * @return Vector of SignetSource objects, or empty Vector (not null!)
	 * 
	 */
	public Vector getSourcesByUsage(String usage)
	{
		Vector retval = new Vector();

		if ((null != usage) && (0 < usage.length()))
		{
			for (Iterator srcs = signetSources.iterator(); srcs.hasNext();)
			{
				SignetSource src = (SignetSource)srcs.next();
				if (src.hasUsage(usage))
					retval.add(src);
			}
		}

		return (retval);
	}

	/**
	 * Returns a Vector of all SignetSource objects that match the given type.
	 * @return Vector of SignetSource objects, or empty Vector (not null!)
	 * 
	 */
	public Vector getSourcesByType(String type)
	{
		Vector retval = new Vector();

		if ((null != type) && (0 < type.length()))
		{
			for (Iterator srcs = signetSources.iterator(); srcs.hasNext();)
			{
				SignetSource src = (SignetSource)srcs.next();
				if (src.isSubjectType(type))
					retval.add(src);
			}
		}

		return (retval);
	}
	 
	/**
	 * Get a Source that matches the given sourceId. It will be a short list so
	 * iterating through it should not be a performance issue. Note that this
	 * method, unlike the other getSource*() methods, also checks the
	 * persistedSource and the sigAppSource (after first checking the "standard"
	 * sources).
	 * @param sourceId The source ID to match
	 * @return The matching SignetSubject or null
	 */
	public SignetSource getSource(String sourceId)
	{
		if ((null == sourceId) || (0 >= sourceId.length()))
			return (null);

		SignetSource retval = null;

		// iterate until found or exhausted the list
		for (Iterator srcs = signetSources.iterator(); srcs.hasNext() && (null == retval);)
		{
			SignetSource src = (SignetSource)srcs.next();
			if (src.getId().equals(sourceId))
				retval = src;
		}

		// if not found, is it in Persisted or AppSource?
		if (null == retval)
		{
			if ((null != persistedSource) && (sourceId.equals(persistedSource.getId())))
				retval = persistedSource;
			else if ((null != sigAppSource) && (sourceId.equals(sigAppSource.getId())))
				retval = sigAppSource;
		}

		return (retval);
	}


	/**
	 * This is considered a low-level method that most applications won't need to deal with.
	 * @return the SourceManager (SubjectAPI) associated with this instance of Signet.
	 */
	public SourceManager getSourceManager()
	{
		return (sourceManager);
	}

	/**
	 * This is considered a low-level method that most applications won't need to deal with.
	 * This is a convenience method that has the same effect as getSource("persisted source id string")
	 * @return the PersistedSignetSource associated with this instance of Signet.
	 */
	public PersistedSignetSource getPersistedSource()
	{
		return (persistedSource);
	}


	/**
	 * @return the instance of Signet
	 */
	public Signet getSignet()
	{
		return (signet);
	}


	////////////////////////////////////
	// overrides Object
	////////////////////////////////////

	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		for (Iterator srcs = signetSources.iterator(); srcs.hasNext();)
			buf.append(srcs.next().toString() + "\n----------\n");

		return (buf.toString());
	}


	//////////////////////////////////
	// testing only
	//////////////////////////////////

	public static void main(String[] args)
	{
System.out.println("main: working dir = " + System.getProperty("user.dir"));
		SignetSources srcs = new SignetSources("/subjectSources.xml", null);
		System.out.println("SignetSources.SignetSources:\n  " + srcs);

	}

}
