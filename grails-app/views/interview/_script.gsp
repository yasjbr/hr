<script type="text/javascript">

    function afterSave() {
        $("#detailsTable tbody.committeeRoleClass").empty();
        index = ${(interview?.id)?(interview?.committeeRoles?.size()+1):1};
    }


    var index = ${(interview?.id)?(interview?.committeeRoles?.size()+1):1};

    function addCommitteeRole() {
        $('.alert.modalPage').html("");

        var committeeRoleSelected = $("#committeeRoleId").val() ? $("#committeeRoleId option:selected").text() : null;
        var committeeRoleSelectedId = $("#committeeRoleId").val() ? $("#committeeRoleId option:selected").val() : null;
        var committeeName = $("#committeeNameId").val() ? $("#committeeNameId").val() : "";


        if (committeeName && committeeRoleSelected && committeeRoleSelectedId) {
            var rowTable /*= "<tr width='100%' id='roles-row-" + index + "' class='tr_" + i + "' style='width: 100% ;border-bottom:1pt dotted #dcebf7; height: 20px !important;'  >";*/

            rowTable += "<tr id='roles-row-" + index + "' class='center' >";
            rowTable += "<td class='center'>" + committeeRoleSelected + "</td>";
            rowTable += "<td class='center'>" + committeeName + "</td>";
            rowTable += "<td class='center'>";

            rowTable += "<input type='hidden' name='committeeRole' value='" + committeeRoleSelectedId + "'>";
            rowTable += "<input type='hidden' name='partyName' value='" + committeeName + "'>";
            rowTable += "<span class='delete-action'><a style='cursor: pointer;' class='red icon-trash ' onclick='deleteRow(" + index + ")' title='<g:message code='default.button.delete.label'/>'></a> </span>";//delete
            rowTable += "</td></tr>";
            //end actions


            jQuery("#detailsTable tbody").append(rowTable);
            showInfo("${message(code:'jobRequisition.previousWork.modal.add.success')}");
            index++;
            $('#roles-row-0').remove();
            resetForm();
        }
        else {
            showError("${message(code:'interview.error2.label')}");

        }
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

    function deleteRow(index) {
        gui.confirm.confirmFunc("${message(code:'default.confirmTitle.label')}", "${message(code:'default.confirm.label')}", function () {
            $('#roles-row-' + index).remove();
            resetcommitteeRolesTable();
        });
    }

    function resetcommitteeRolesTable(){
        if($("#detailsTable tbody tr.center").length == 0) {
            var rowTable //= "<rowElement>";
            rowTable += "<tr id='roles-row-0' class='center' >";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "</tr>";
            $("#detailsTable").append(rowTable);
        }
    }

    function resetForm() {
        gui.autocomplete.clear("committeeRoleId");
//        $("#committeeRoleId").text = "";
//        $("#committeeRoleId").val("");
        $('#committeeNameId').val("");
    }

    function closeCommitteeRoleModal() {
        $("#committeeRoleModal").modal('hide');
    }

    function openCommitteeRoleModal() {
        $('.alert.modalPage').html("");
        resetForm();
        $("#committeeRoleModal").modal('show');
    }
    %{-- ************* --}%


    %{-- Related to Inspection Logic--}%

    var mandatoryInspection = [];

    function InspectionCategoriesParams() {
        var searchParams = {};
        searchParams.isRequiredByFirmPolicy = false;
        return searchParams;
    }

    function removeCloseBtn() {
        var selectionRendered = $(".inspectionCategoriesDiv").find(".select2-selection__rendered");
        for (var count = 0; count < mandatoryInspection.length; count++) {
            var li = selectionRendered.find("li[title='" + mandatoryInspection[count] + "']");
            li.addClass("mandatoryInspection");
            li.css("background-color", "rgba(32, 98, 213, 0.34)");
            li.find("span").remove();
        }
        if (mandatoryInspection.length > 0) {
            selectionRendered.find(".select2-selection__clear").remove();
        }
    }

    $("#inspectionCategories").on('select2:select', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:selecting', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('change', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:loaded', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:removed', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:open', function (evt) {
        removeCloseBtn();
    });

    %{-- ***************************--}%



    function  interviewParams() {
        var searchParams = {};
        searchParams.requisitionAnnouncementStatus ='${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.INTERVIEW}';
        return searchParams;
    }
</script>