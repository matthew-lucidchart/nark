package com.lucidchart.open.nark.models.records

import java.util.{Date, UUID}
import scala.math.BigDecimal

object Comparisons extends Enumeration {
	val < = Value(0, "<")
	val <= = Value(1, "<=")
	val == = Value(2, "==")
	val >= = Value(3, ">=")
	val > = Value(4, ">")
	val != = Value(5, "!=")
	val isNull = Value(6, "Is NULL")
	val isNotNull = Value(7, "Is Not NULL")

	val nullables = ValueSet(
		isNull,
		isNotNull
	)
}

object AlertStatus extends Enumeration {
	val success = Value(0, "success")
	val failure = Value(1, "failure")
}

object AlertState extends Enumeration {
	val normal = Value(0, "normal")
	val error = Value(1, "error")
	val warn = Value(2, "warn")
}

object AlertType extends Enumeration {
	val alert = Value(0, "alert")
	val dynamicAlert = Value(1, "dynamic alert")
}

case class Alert (
	id: UUID,
	name: String,
	userId: UUID,
	target: String,
	comparison: Comparisons.Value,
	dynamicAlertId: Option[UUID],
	active: Boolean,
	deleted: Boolean,
	created: Date,
	updated: Date,
	threadId: Option[UUID],
	threadStart: Option[Date],
	lastChecked: Date,
	nextCheck: Date,
	frequency: Int,
	warnThreshold: BigDecimal,
	errorThreshold: BigDecimal,
	worstState: AlertState.Value,
	consecutiveFailures: Int,
	dataSeconds: Int,
	dropNullPoints: Int,
	dropNullTargets: Boolean
) extends AppRecord with HasId {
	/**
	 * Create a new Alert record for inserting into the database
	 */
	def this(name: String, userId: UUID, dynamicAlertId: Option[UUID], target: String, comparison: Comparisons.Value, frequency: Int, warnThreshold: BigDecimal, errorThreshold: BigDecimal, dataSeconds: Int, dropNullPoints: Int, dropNullTargets: Boolean) = this(UUID.randomUUID(), name, userId, target, comparison, dynamicAlertId, true, false, new Date(), new Date(), None, None, new Date(), new Date(), frequency, warnThreshold, errorThreshold, AlertState.normal, 0, dataSeconds, dropNullPoints, dropNullTargets)
	def this(name: String, userId: UUID, target: String, comparison: Comparisons.Value, frequency: Int, warnThreshold: BigDecimal, errorThreshold: BigDecimal, dataSeconds: Int, dropNullPoints: Int, dropNullTargets: Boolean) = this(name, userId, None, target, comparison, frequency, warnThreshold, errorThreshold, dataSeconds, dropNullPoints, dropNullTargets)
}