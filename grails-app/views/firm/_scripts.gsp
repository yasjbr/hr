<script>
    function saveOrganization() {
        if ($("#corporationClassificationId").val()) {
            var corporationClassification = $("#corporationClassificationId").val()
            var parentOrganization = $("#parentOrganizationId").val()
            var localName = $("#localNameWithParent").val()
            var latinName = $("#latinNameId").val()
            var hebrewName = $("#hebrewNameId").val()
            var organizationMainActivity = $("#organizationMainActivityId").val()
            var latinDescription = $("#latinDescriptionId").val()
            var localDescription = $("#localDescriptionId").val()
            var missionStatement = $("#missionStatementId").val()
            var organizationType = $("#organizationTypeId").val()
            var registrationNumber = $("#registrationNumberId").val()
            var tax = $("#taxId").val()
            var workingSector = $("#workingSectorId").val()
            $.ajax({
                url: "${createLink(controller: 'firm',action: 'saveOrganization')}",
                data: {
                    'corporationClassification.id': corporationClassification,
                    "parentOrganization.id": parentOrganization,
                    "localName": localName,
                    "latinName": latinName,
                    "hebrewName": hebrewName,
                    "organizationMainActivity.id": organizationMainActivity,
                    "latinDescription": latinDescription,
                    "localDescription": localDescription,
                    "missionStatement": missionStatement,
                    "organizationType.id": organizationType,
                    "registrationNumber": registrationNumber,
                    "tax": tax,
                    "workingSector.id": workingSector
                },
                type: "GET",
                dataType: "json",
                success: function (data) {
                    var newOption = new Option(data.organizationName, data.id, true, true);
                    $('#organizationAutoComplete').append(newOption);
                    $('#organizationAutoComplete').trigger('change');
                    $("#corporationClassificationId").val()
                    $("#corporationClassificationId").trigger('change');
                    $("#parentOrganizationId").val()
                    $("#parentOrganizationId").trigger('change');
                    $("#localNameWithParent").val()
                    $("#latinNameId").val()
                    $("#hebrewNameId").val()
                    $("#organizationActivityId").val()
                    $("#organizationActivityId").trigger('change');
                    $("#latinDescriptionId").val()
                    $("#localDescriptionId").val()
                    $("#missionStatementId").val()
                    $("#organizationTypeId").val()
                    $("#organizationTypeId").trigger('change');
                    $("#registrationNumberId").val()
                    $("#taxId").val()
                    $("#workingSectorId").val()
                    $("#workingSectorId").trigger('change');
                    $('#modal-form').modal('hide');
                },
            });
        }
    }
    $(document).ready(function () {
        $('#modal-form').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form'));
        });
    });


</script>