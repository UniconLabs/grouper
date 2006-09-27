/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;
import  junit.framework.*;

/**
 * @author  blair christensen.
 * @version $Id: SuiteMemberOf.java,v 1.4 2006-09-27 14:15:30 blair Exp $
 */
public class SuiteMemberOf extends TestCase {
  
  public SuiteMemberOf(String name) {
    super(name);
  } // public SuiteMemberOf(name)

  static public Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite( TestMemberOf0.class   );  // eff mship uuid does not change
    suite.addTestSuite( TestMemberOf1.class   );  // Forward MemberOf deletion.
    // TODO 20060927 Split pre-existing tests
    suite.addTestSuite(TestMemberOf.class   );
    return suite;
  } // static public Test suite()

}

