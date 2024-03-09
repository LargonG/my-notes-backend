package org.kote.integration

trait Integration[K, V] {
  def getIntegrationData(id: K): V
}
