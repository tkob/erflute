package org.dbflute.erflute.editor.controller.editpart.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relation.ChangeRelationPropertyCommand;
import org.dbflute.erflute.editor.controller.editpolicy.element.connection.CommentConnectionEditPolicy;
import org.dbflute.erflute.editor.controller.editpolicy.element.connection.ERDiagramBendpointEditPolicy;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.view.dialog.relationship.RelationDialog;
import org.dbflute.erflute.editor.view.figure.connection.ERDiagramConnection;
import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

public class CommentConnectionEditPart extends ERDiagramConnectionEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        boolean bezier = this.getDiagram().getDiagramContents().getSettings().isUseBezierCurve();
        PolylineConnection connection = new ERDiagramConnection(bezier);
        connection.setConnectionRouter(new BendpointConnectionRouter());

        connection.setLineStyle(SWT.LINE_DASH);

        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
        this.installEditPolicy(EditPolicy.CONNECTION_ROLE, new CommentConnectionEditPolicy());
        this.installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new ERDiagramBendpointEditPolicy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshBendpoints() {
        // ベンド・ポイントの位置情報の取得
        ConnectionElement connection = (ConnectionElement) this.getModel();

        // 実際のベンド・ポイントのリスト
        List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();

        for (Bendpoint bendPoint : connection.getBendpoints()) {
            constraint.add(new AbsoluteBendpoint(bendPoint.getX(), bendPoint.getY()));
        }

        this.getConnectionFigure().setRoutingConstraint(constraint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(Request request) {
        Relationship relation = (Relationship) this.getModel();

        if (request.getType().equals(RequestConstants.REQ_OPEN)) {
            Relationship copy = relation.copy();

            RelationDialog dialog = new RelationDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);

            if (dialog.open() == IDialogConstants.OK_ID) {
                ChangeRelationPropertyCommand command = new ChangeRelationPropertyCommand(relation, copy);
                this.getViewer().getEditDomain().getCommandStack().execute(command);
            }
        }

        super.performRequest(request);
    }
}
