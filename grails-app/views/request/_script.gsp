<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<%--
  Created by IntelliJ IDEA.
  User: Muath
  Date: 25/07/17
  Time: 1:19
--%>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>


<el:modalLink id="requestOrderInfoLink" link="#" style="display: none;"
              preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
              label="${message(code: 'employeeViolation.entities')}">
    <i class="icon-list"></i>
</el:modalLink>

<script>


    function manageExecuteEdit(row) {
        if (row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}") {
            return true;
        }
        return false;
    }

    function manageExecuteDelete(row) {
        return manageExecuteEdit(row);
    }


    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    function manageListLink(row) {
        return row.includedInList == 'true';
    }

    function showSetOrderInfo(row) {
        return row.canSetOrderInfo == 'true';
    }

    function showSetExternalOrderInfo(row) {
        return row.canSetExternalOrderInfo == 'true';
    }

    /**
     * show stop allowance only when allowance is start
     * @param row
     * @returns {boolean}
     */
    function manageStopRequest(row) {
        return row.canStopRequest == 'true';
    }


    /**
     * Show continue allowance only when allowance is start
     * @param row
     * @returns {boolean}
     */
    function manageExtendRequest(row) {
        return row.canExtendRequest == 'true';
    }

    /**
     * show cancel allowance only when allowance is start
     * @param row
     * @returns {boolean}
     */
    function manageCancelRequest(row) {
        return row.canCancelRequest == 'true';
    }

    function manageEditRequest(row) {
        return row.canEditRequest == 'true';
    }

    function viewOrderInfoModal(requestId, encodedId) {

        var tableId= $('.function_'+requestId).closest('table').attr('id');

        var link = "${createLink(controller: 'request',action: 'setInternalManagerialOrder')}?encodedId="+encodedId+"&dataTableId=" + tableId;

        $('#requestOrderInfoLink').attr("href", link);
        $('#requestOrderInfoLink').click();
    }

    function viewExternalOrderInfoModal(requestId, encodedId) {

        var tableId= $('.function_'+requestId).closest('table').attr('id');

        var link = "${createLink(controller: 'request',action: 'setExternalManagerialOrder')}?encodedId="+encodedId+"&dataTableId=" + tableId;

        $('#requestOrderInfoLink').attr("href", link);
        $('#requestOrderInfoLink').click();
    }

</script>