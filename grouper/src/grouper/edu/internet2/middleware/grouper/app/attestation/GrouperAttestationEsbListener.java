package edu.internet2.middleware.grouper.app.attestation;

import java.util.List;

import edu.internet2.middleware.grouper.changeLog.esb.consumer.EsbEventContainer;
import edu.internet2.middleware.grouper.esb.listener.EsbListenerBase;
import edu.internet2.middleware.grouper.esb.listener.ProvisioningSyncConsumerResult;

public class GrouperAttestationEsbListener extends EsbListenerBase {

  @Override
  public boolean dispatchEvent(String eventJsonString, String consumerName) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void disconnect() {
    // TODO Auto-generated method stub
  }

  @Override
  public ProvisioningSyncConsumerResult dispatchEventList(List<EsbEventContainer> esbEventContainers) {

    ProvisioningSyncConsumerResult provisioningSyncConsumerResult = new ProvisioningSyncConsumerResult();
    new GrouperAttestationDaemonLogic().incrementalLogic(esbEventContainers);
    
    provisioningSyncConsumerResult.setLastProcessedSequenceNumber(esbEventContainers.get(esbEventContainers.size()-1).getSequenceNumber());
    
    return provisioningSyncConsumerResult;
    
  }
  
  

}
