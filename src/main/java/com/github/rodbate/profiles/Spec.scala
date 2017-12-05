package com.github.rodbate.profiles

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component




@Component
class Spec (@Value("#{systemProperties['os.name']}") version: String) {
  def getVersion: String = version
}
