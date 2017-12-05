package com.github.rodbate.profiles

import org.springframework.context.annotation.{Condition, ConditionContext}
import org.springframework.core.`type`.AnnotatedTypeMetadata


class ScalaCondition extends Condition {
  def matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean = {

    val map = metadata.getAnnotationAttributes(classOf[Annot].getName)
    val map1 = metadata.getAllAnnotationAttributes(classOf[Annot].getName)

    println(map.get("a"))
    true
  }
}

object ScalaCondition {

}