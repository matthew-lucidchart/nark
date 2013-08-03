package com.lucidchart.open.nark.controllers

import com.lucidchart.open.nark.request.{AppFlash, AppAction, AuthAction}
import com.lucidchart.open.nark.views
import com.lucidchart.open.nark.models.{TargetModel, GraphTypes, GraphModel, DashboardModel}
import com.lucidchart.open.nark.models.records.{Target, Graph}
import java.util.UUID
import views.html.dashboards.dashboard
import play.api.data.Form
import play.api.data.Forms._
import com.lucidchart.open.nark.models.records.Target
import play.api.data.validation.Constraints

class TargetsController extends AppController {

	private case class AddTarget(target: String)
	private val addForm = Form(
		mapping(
			"target" -> text
		)(AddTarget.apply)(AddTarget.unapply)
	)


	def add(graphId: UUID) = AuthAction.authenticatedUser { implicit user =>
		AppAction { implicit request =>
			val graph = GraphModel.findGraphByID(graphId)
			if (graph.isEmpty) {
				Redirect(routes.HomeController.index()).flashing(AppFlash.warning("Graph does not exist."))
			}
			else if (graph.get.userId != user.id) {
				Redirect(routes.HomeController.index()).flashing(AppFlash.warning("Graph does not belong to the current user."))
			}
			else {
				val form = addForm.fill(AddTarget(""))
				val dashboard = DashboardModel.findDashboardByID(graph.get.dashboardId)
				Ok(views.html.targets.add(form, graph.get, dashboard.get))
			}
		}
	}

	def addSubmit(graphId: UUID) = AuthAction.authenticatedUser { implicit user =>
		AppAction { implicit request =>
			val graph = GraphModel.findGraphByID(graphId)
			if (graph.isEmpty) {
				Redirect(routes.HomeController.index()).flashing(AppFlash.warning("Graph does not exist."))
			}
			addForm.bindFromRequest().fold(
				formWithErrors => {
					println(formWithErrors)
					val dashboard = DashboardModel.findDashboardByID(graph.get.dashboardId)
					Ok(views.html.targets.add(formWithErrors, graph.get, dashboard.get))
				},
				data => {
					if (graph.get.userId != user.id) {
						Redirect(routes.HomeController.index()).flashing(AppFlash.warning("Graph does not belong to the current user."))
					}
					else {
						val data : Map[String, Seq[String]] = request.body.asFormUrlEncoded.getOrElse(Map())
						val target = data("target").head
						TargetModel.createTarget(new Target(graph.get.id, target, user.id, false))
						Redirect(routes.TargetsController.add(graphId)).flashing(AppFlash.success("Target added successfully."))
					}
				}
			)
		}
	}

	def list(graphId: UUID) = AuthAction.authenticatedUser { implicit user =>
		AppAction { implicit request =>
			val graph = GraphModel.findGraphByID(graphId)
			if (graph.isEmpty) {
				Redirect(routes.HomeController.index()).flashing(AppFlash.warning("Graph does not exist."))
			}
			else {
				val targets = TargetModel.findTargetByGraphId(graph.get.id)
				Ok(views.html.targets.list(targets, graph.get))
			}
		}
	}

	def toggleActivation(uuid: UUID) = AuthAction.authenticatedUser { implicit user =>
		AppAction { implicit request =>
			val target = TargetModel.findTargetByID(uuid)
			if (target.isEmpty) {
				Redirect(routes.HomeController.index()).flashing(AppFlash.warning("Target does not exist."))
			}
			else if (target.get.userId != user.id) {
				Redirect(routes.HomeController.index()).flashing(AppFlash.warning("Target does not belong to the current user."))
			}
			else {
				val toggleForm = if (target.get.deleted) "activated" else "deactivated"
				TargetModel.toggleActivation(target.get)
				val returnUrl = request.queryString.get("return_url").flatMap(_.headOption)
				if (returnUrl.isDefined) {
					Redirect(returnUrl.get).flashing(AppFlash.success("Target " + toggleForm + "."))
				}
				else {
					Redirect(routes.HomeController.index()).flashing(AppFlash.success("Target " + toggleForm + "."))
				}
			}
		}
	}
}

object TargetsController extends TargetsController