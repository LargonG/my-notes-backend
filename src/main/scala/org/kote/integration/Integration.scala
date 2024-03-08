package org.kote.integration

trait Integration[ID, REMOTE_DATA] {
  def getIntegrationData(id: ID): REMOTE_DATA
}
