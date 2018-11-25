<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyClass; ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus" %>
<%--
  Created by IntelliJ IDEA.
  User: wassi
  Date: 09/08/17
  Time: 1:19
--%>


<script>


    $(document).ready(function () {

        $('#modal-form').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form'));
        });
        $('#modal-form1').on('shown.bs.modal', function () {
            _dataTables['allowanceRequestTableToChooseInAllowance'].draw();
            gui.initAllForModal.init($('#modal-form1'));
        });
        $('#modal-form2').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form2'));
        });
        $('#modal-form3').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form3'));
        });
        $('#modal-form4').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form4'));
        });
        $('#modal-form5').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form5'));
        });
        $('#modal-form6').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form6'));
        });
    });

    /*allow delete when recordStatus is NEW  */
    function manageExecuteActions(row) {
        if (${aocCorrespondenceList?.currentStatus in [EnumCorrespondenceStatus.CREATED, EnumCorrespondenceStatus.NEW]}) {
            return true;
        }
        return false;
    }


    /*to allow add note to request until close the list*/
    function showNoteInList() {
        return true;
    }

    function showCreateNote() {
        return showChangeStatus();
    }


    function showChangeStatus() {
        if (${aocCorrespondenceList?.currentStatus in [EnumCorrespondenceStatus.APPROVED, EnumCorrespondenceStatus.PARTIALLY_APPROVED,
                        EnumCorrespondenceStatus.REJECTED, EnumCorrespondenceStatus.SUBMITTED, EnumCorrespondenceStatus.FINISHED]}) {
            return false
        }
        if (${aocCorrespondenceList?.currentStatus==EnumCorrespondenceStatus.IN_PROGRESS && workflowPathHeader ==null}) {
            return false;
        }
        return true;
    }

    function removeAocRecord(encodedId) {
        var listId = $('#aocCorrespondenceListId').val();
        gui.confirm.confirmFunc("", "${message(code: 'list.removeRecord.label', args: [entity], default: 'remove record')}", function () {
            $.ajax({
                url: "${createLink(controller: 'aocListRecord', action: 'delete')}",
                type: 'POST',
                data: {
                    encodedId: encodedId,
                    aocCorrespondenceListId: listId
                },
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    $('.alert.page').html('');
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (data) {
                    _dataTables['listRecordTableInAocList'].draw();
                    guiLoading.hide();
                }
            });
        });
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

    function callBackWorkflowFunction(json) {
        if (json.success) {
            window.location.href = '${createLink(controller:'aocCorrespondenceList', action: 'listWorkflow')}';
        }
    }


    var index = ${(aocCorrespondenceList?.id)?(aocCorrespondenceList?.copyToPartyList?.size()+1):1};

    function addCopyToParty() {
        $('.alert.modalPage').html("");
        var partyType = "${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType.COPY.toString()}";
        var partyClass = $("#" + partyType + "_partyClassSelect option:selected").text();
        var partyClassId = $("#" + partyType + "_partyClassSelect option:selected").val() ? $("#" + partyType + "_partyClassSelect option:selected").val() : null
        var partyName = $("#" + partyType + "_" + partyClassId + "Id").val() ? $("#" + partyType + "_" + partyClassId + "Id").text() : null
        var partyId = $("#" + partyType + "_" + partyClassId + "Id").val() ? $("#" + partyType + "_" + partyClassId + "Id").val() : null

        if (partyClassId == null || partyId == null) {
            showError("${message(code:'jobRequisition.error1.label')}");
        } else {
            var rowTable = "<tr id='row-" + index + "' class='center' >";
            rowTable += "<td class='center'>" + index + "</td>";
            rowTable += "<td class='center'>" + partyClass + "</td>";
            rowTable += "<td class='center'>" + partyName + "</td>";
            rowTable += "<td class='center'>";
            rowTable += "<input type='hidden' name='partyTypeCopy'  id='partyType-" + index + "' value='" + partyType + "'>";
            rowTable += "<input type='hidden' name='partyClassCopy'  id='partyClass-" + index + "' value='" + partyClassId + "'>";
            rowTable += "<input type='hidden' name='partyIdCopy'  id='partyId-" + index + "' value='" + partyId + "'>";
            rowTable += "<span class='delete-action'><a style='cursor: pointer;' class='red icon-trash ' onclick='deleteRow(" + index + ")' title='<g:message code='default.button.delete.label'/>'></a> </span>";//delete
            rowTable += "</td>" +
                "</tr>" //+ "</rowElement>";
            //end actions
            $("#copyToPartyTable").append(rowTable);
            showInfo("${message(code:'aocCorrespondence.copyToParty.modal.add.success')}");
            index++;
            $('#row-0').remove();
            resetForm();
        }
    }

    function closeCopyToModal() {
        $('#application-modal-main-content').modal("hide");
    }

    function deleteRow(index) {
        gui.confirm.confirmFunc("${message(code:'default.confirmTitle.label')}", "${message(code:'default.confirm.label')}", function () {
            $('#row-' + index).remove();
            resetCopyToTableTable();
        });
    }

    function resetCopyToTableTable() {
        if ($("#copyToPartyTable tbody tr.center").length == 0) {
            var rowTable = "<tr id='row-0' class='center' >";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "</tr>";
            $("#copyToPartyTable").append(rowTable);
        }
    }

    function resetForm() {
        var partyType = "${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType.COPY.toString()}";
        gui.autocomplete.clear(partyType + "_${EnumCorrespondencePartyClass.FIRM}Id");
        gui.autocomplete.clear(partyType + "_${EnumCorrespondencePartyClass.COMMITTEE}Id");
        gui.autocomplete.clear(partyType + "_${EnumCorrespondencePartyClass.ORGANIZATION}Id");
    }


    function showError(errorMessage) {
        var msg = "<div class='alert alert-block alert-danger'>" +
            "<button data-dismiss='alert' class='close' type='button'>" +
            "<i class='ace-icon fa fa-times'>" +
            "</i>" +
            "</button>" +
            "<ul>" +
            "<li>" + errorMessage + "</li> " +
            "</ul>" +
            "</div>";
        $('.alert.modalPage').html(msg);
    }

    function showInfo(infoMessage) {
        var msg = "<div class='alert alert-block alert-success'>" +
            "<button data-dismiss='alert' class='close' type='button'>" +
            "<i class='ace-icon fa fa-check'>" +
            "</i>" +
            "</button>" +
            "<ul>" +
            "<li>" + infoMessage + "</li> " +
            "</ul>" +
            "</div>";
        $('.alert.modalPage').html("");
        $('.alert.modalPage').html(msg);
    }


    /**
     * to filter province locations by selected province
     */
    function provinceParams() {
        var searchParams = {};
        searchParams["province.id"] = $('#provinceId').val();
        return searchParams;
    }


    /**
     * to reset province location based on province changes.
     */
    function resetProvinceLocation() {
        gui.autocomplete.clear("provinceLocationId");
    }
</script>