<script type="text/javascript">
    var mandatoryInspection = [];
    var educationDegreeList = [];
    var educationMajorList = [];


    function afterSave() {
        $("#previousWorkTable tbody.workExperiences").empty();
        index = ${(vacancy?.id)?(vacancy?.requisitionWorkExperiences?.size()+1):1};
    }

    var index = ${(vacancy?.id)?(vacancy?.requisitionWorkExperiences?.size()+1):1};

    //to add new work experience to the table of work experience
    function addPreviousWork() {
        $('.alert.modalPage').html("");
        var professionType = $("#workExperience-professionType option:selected").text();
        var professionTypeId = $("#workExperience-professionType option:selected").val() ? $("#workExperience-professionType option:selected").val() : null
        var competency = $("#workExperience-competency option:selected").text();
        var competencyId = $("#workExperience-competency option:selected").val() ? $("#workExperience-competency option:selected").val() : null
        var periodInYears = $("#periodInYears_").val() ? $("#periodInYears_").val() : null
        var otherSpecifications = $("textarea#otherSpecifications_").val() ? $("textarea#otherSpecifications_").val() : ""

        if (periodInYears == null) {
            showError("${message(code:'vacancy.error2.label')}");
        } else if (professionTypeId == null && competencyId == null && otherSpecifications == "") {
            showError("${message(code:'vacancy.error1.label')}");
        } else {
            var rowTable = "<tr id='row-" + index + "' class='center' >";
            rowTable += "<td class='center'>" + periodInYears + "</td>";
            rowTable += "<td class='center'>" + professionType + "</td>";
            rowTable += "<td class='center'>" + competency + "</td>";
            rowTable += "<td class='center'>" + otherSpecifications + "</td>";
            rowTable += "<td class='center'>";
            rowTable += "<input type='hidden' name='professionType'  id='professionType-" + index + "' value='" + professionTypeId + "'>";
            rowTable += "<input type='hidden' name='competency'  id='competency-" + index + "' value='" + competencyId + "'>";
            rowTable += "<input hidden type='number' name='periodInYears'  id='periodInYears-" + index + "' value='" + periodInYears + "'>";
            rowTable += "<input type='hidden' name='otherSpecifications'  id='otherSpecifications-" + index + "' value='" + otherSpecifications + "'>";
            rowTable += "<span class='delete-action'><a style='cursor: pointer;' class='red icon-trash ' onclick='deleteRow(" + index + ")' title='<g:message code='default.button.delete.label'/>'></a> </span>";//delete
            rowTable += "</td></tr>";
            //end actions

            $("#previousWorkTable").append(rowTable);
            showInfo("${message(code:'jobRequisition.previousWork.modal.add.success')}");
            index++;
            $('#row-0').remove();
            resetForm();
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
            $('#row-' + index).remove();
            resetPreviousWorkTable();
        });
    }

    function resetPreviousWorkTable(){
        if($("#previousWorkTable tbody tr.center").length == 0) {
            var rowTable //= "<rowElement>";
            rowTable += "<tr id='row-0' class='center' >";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "</tr>";
            $("#previousWorkTable").append(rowTable);
        }
    }

    //to reset the table of work requisition
    function resetForm() {
        gui.autocomplete.clear("workExperience-professionType");
        gui.autocomplete.clear("workExperience-competency");
        $('#otherSpecifications_').val("");
        $('#periodInYears_').val("");
    }

    function closePreviousWorkModal() {
        $("#previousWorkModal").modal('hide');
    }

    function openPreviousWorkModal() {
        $('.alert.modalPage').html("");
        resetForm();
        $("#previousWorkModal").modal('show');
    }
    %{-- Related to Inspection Logic--}%


    function InspectionCategoriesParams() {
        var searchParams = {};
        searchParams.isRequiredByFirmPolicy = false;
        searchParams.allInspectionCategory = true;
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


    function removeCloseBtnFromEducationDegree() {
        var selectionRendered = $(".educationDegreesDiv").find(".select2-selection__rendered");
        for (var count = 0; count < educationDegreeList.length; count++) {
            var li = selectionRendered.find("li[title='" + educationDegreeList[count] + "']");
            li.addClass("educationDegreeList");
            li.css("background-color", "rgba(32, 98, 213, 0.34)");
            li.find("span").remove();
        }
        if (educationDegreeList.length > 0) {
            selectionRendered.find(".select2-selection__clear").remove();
        }
    }


    function removeCloseBtnFromEducationMajor() {
        var selectionRendered = $(".educationMajorDiv").find(".select2-selection__rendered");
        for (var count = 0; count < educationMajorList.length; count++) {
            var li = selectionRendered.find("li[title='" + educationMajorList[count] + "']");
            li.addClass("educationMajorList");
            li.css("background-color", "rgba(32, 98, 213, 0.34)");
            li.find("span").remove();
        }
        if (educationMajorList.length > 0) {
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

    $(document).ready(function () {
        $(window).load(function () {
            var $el = $("#inspectionCategories");
            var selectionRendered = $(".inspectionCategoriesDiv").find(".select2-selection__rendered");

            $.ajax({
                url: "<g:createLink controller="vacancy" action="getMandatoryInspection"/>",
                type: 'POST',
                beforeSend: function () {
                },
                complete: function () {
                },
                success: function (data) {

                    for (var i = 0; i < data.length; i++) {
                        var text = data[i].text + " (إلزامي) ";
                        var id = data[i].id;
                        mandatoryInspection.push(text);
                        var newOption = new Option(text, id, true, true);
                        $el.append(newOption);
                        $el.trigger('change');
                    }
                    removeCloseBtn();
                },
                error: function (request, status, error) {

                }
            });
            $el.on('select2:select', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:selecting', function (evt) {
                removeCloseBtn();
            });
            $el.on('change', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:loaded', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:removed', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:open', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:focus', function (evt) {
                removeCloseBtn();
            });
        });
    });

    //to reset the table of job requisition
    function resetJobRequisition() {

        $("#previousWorkTable tbody tr").remove();
        gui.autocomplete.clear("recruitmentCycle-id");
        gui.autocomplete.clear("requestedForDepartment");
        gui.autocomplete.clear("job-id");
        gui.autocomplete.clear("jobType-id");
        gui.autocomplete.clear("governorates");
        //to remove all options from multi select auto complete
        $("#governorates").html("");
        gui.autocomplete.clear("fromGovernorates");
        $("#fromGovernorates").html("");
        gui.autocomplete.clear("educationDegrees");
        $("#educationDegrees").html("");
        gui.autocomplete.clear("educationMajors");
        $("#educationMajors").html("");
        $('#numberOfPositions').val("");
        gui.autocomplete.clear("proposedRank-id");
        gui.autocomplete.clear("inspectionCategories");
        $("#inspectionCategories").html("");
        $('#jobDescription').val("");
        $('#fromAge').val("");
        $('#toAge').val("");
        $('#fromTall').val("");
        $('#toTall').val("");
        $('#fromWeight').val("");
        $('#toWeight').val("");
        $('#note').val("");
        gui.autocomplete.clear("maritalStatusId");

    }


    //    to get the information of job requisition
    function getJobRequisitionInfo() {
        $.ajax({
            url: '${createLink(controller: 'vacancy',action: 'getJobRequisitionInfo')}',
            type: 'POST',
            data: $('#jobRequsitionDataTableForm').serialize(),
            dataType: 'json',
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (json) {
                guiLoading.hide();
                // to reset data from the form create
                resetJobRequisition();
                $("#jobRequisitionId").val(json.data.id);
                // to append data in the appropiate feilds
                $("#recruitmentCycle-id").val(json.data.recruitmentCycleId);

                var newOption = new Option(json.data.recruitmentCycleName, json.data.recruitmentCycleId, true, true);

                if (json.data.recruitmentCycleId && json.data.recruitmentCycleId) {
                    $('#recruitmentCycle-id').append(newOption);
                    $('#recruitmentCycle-id').trigger('change');
                }

                $("#requestedForDepartment").val(json.data.requestedForDepartmentId);
                var newOption = new Option(json.data.requestedForDepartmentName, json.data.requestedForDepartmentId, true, true);
                $('#requestedForDepartment').append(newOption);
                $('#requestedForDepartment').trigger('change');

                $("#job").val(json.data.jobId);
                var newOption = new Option(json.data.jobName, json.data.jobId, true, true);
                $('#job').append(newOption);
                $('#job').trigger('change');

                $("#jobType-id").val(json.data.jobTypeId);
                var newOption = new Option(json.data.jobTypeName, json.data.jobTypeId, true, true);
                $('#jobType-id').append(newOption);
                $('#jobType-id').trigger('change');
                $("#governorates").val(json.data.governorates);
                if (json.data.governorates.length > 0 && json.data.governorateMapList) {
                    for (indx = 0; indx < json.data.governorateMapList.length; indx++) {
                        var newOption = new Option(json.data.governorateMapList[indx], json.data.governorates[indx], true, true);
                        $('#governorates').append(newOption);
                        $('#governorates').trigger('change');
                    }
                }

                $("#fromGovernorates").val(json.data.fromGovernorates);
                if (json.data.fromGovernorates.length > 0 && json.data.fromGovernorateMapList) {
                    for (indx = 0; indx < json.data.fromGovernorateMapList.length; indx++) {
                        var newOption = new Option(json.data.fromGovernorateMapList[indx], json.data.fromGovernorates[indx], true, true);
                        $('#fromGovernorates').append(newOption);
                        $('#fromGovernorates').trigger('change');
                    }
                }

                $("#educationDegrees").val(json.data.educationDegrees);
                if (json.data.educationDegrees.length > 0 && json.data.educationDegreeMapList) {
                    for (indx = 0; indx < json.data.educationDegreeMapList.length; indx++) {
                        var newOption = new Option(json.data.educationDegreeMapList[indx], json.data.educationDegrees[indx], true, true);
                        $('#educationDegrees').append(newOption);
                        $('#educationDegrees').trigger('change');
                    }
                }


                $("#educationMajors").val(json.data.educationMajors);
                if (json.data.educationMajors.length > 0 && json.data.educationMajorMapList) {
                    for (indx = 0; indx < json.data.educationMajorMapList.length; indx++) {
                        var newOption = new Option(json.data.educationMajorMapList[indx], json.data.educationMajors[indx], true, true);
                        $('#educationMajors').append(newOption);
                        $('#educationMajors').trigger('change');
                    }
                }


                $("#numberOfPositions").val(json.data.numberOfPositions);
                var newOption = new Option(json.data.numberOfPositions, true);
                $('#numberOfPositions').append(newOption);
                $('#numberOfPositions').trigger('change');


                $("#proposedRank-id").val(json.data.proposedRankId);
                var newOption = new Option(json.data.proposedRankName, json.data.proposedRankId, true, true);
                if (newOption) {
                    $('#proposedRank-id').append(newOption);
                    $('#proposedRank-id').trigger('change');
                }

                var $el = $("#inspectionCategories");

                var newOption = new Option("", "", true, true);
                $("#inspectionCategories").text(newOption);
                $("#inspectionCategories").trigger('change');


                //to remove the inspection category  from autoComplete and set it is new inspection category from job requisition
                $("#inspectionCategories").val(json.data.inspectionCategoriesId);
                for (indx = 0; indx < json.data.inspectionCategoriesName.length; indx++) {

                    var id = json.data.inspectionCategoriesId[indx];
                    var text = json.data.inspectionCategoriesName[indx] + " (إلزامي) ";
                    var newOption = new Option(text, id, true, true);
                    $el.append(newOption);
                    $el.trigger('change');
                    mandatoryInspection.push(text);
                }

                /*to set mandatory inspection */
                if (json.data.inspectionCategoriesRequiredName.length > 0) {
                    for (indx = 0; indx < json.data.inspectionCategoriesRequiredName.length; indx++) {
                        var id = json.data.inspectionCategoriesRequiredName[indx].id;
                        var text = json.data.inspectionCategoriesRequiredName[indx].text + " (إلزامي) ";
                        mandatoryInspection.push(text);
                        var newOption = new Option(text, id, true, true);
                        $el.append(newOption);
                        $el.trigger('change');
                    }
                }


                $("#jobDescription").val(json.data.jobDescription);
                var newOption = new Option(json.data.jobDescription, true);
                $('#jobDescription').append(newOption);
                $('#jobDescription').trigger('change');


                //to draw requisition Work Experiences table
                var professionTypeName, professionTypeId, competencyName, competencyId, periodInYears, otherSpecifications;

                for (index1 = 0; index1 < json.data.requisitionWorkExperiences.length; index1++) {

                    professionTypeName = json.data.requisitionWorkExperiences[index1].professionTypeName ? json.data.requisitionWorkExperiences[index1].professionTypeName : ""
                    professionTypeId = json.data.requisitionWorkExperiences[index1].professionTypeId ? json.data.requisitionWorkExperiences[index1].professionTypeId : ""
                    competencyName = json.data.requisitionWorkExperiences[index1].competencyName ? json.data.requisitionWorkExperiences[index1].competencyName : ""
                    competencyId = json.data.requisitionWorkExperiences[index1].competencyId ? json.data.requisitionWorkExperiences[index1].competencyId : ""
                    periodInYears = json.data.requisitionWorkExperiences[index1].periodInYears ? json.data.requisitionWorkExperiences[index1].periodInYears : ""
                    otherSpecifications = json.data.requisitionWorkExperiences[index1].otherSpecifications ? json.data.requisitionWorkExperiences[index1].otherSpecifications : ""

                    var rowTable = "<tr id='row-" + index + "'>";
                    rowTable += "<td><input type='hidden' name='periodInYears' value='" + periodInYears + "' />" + periodInYears + "</td>";
                    rowTable += "<td><input type='hidden' name='professionTypeIds' value='" + professionTypeId + "' />" + professionTypeName + "</td>";
                    rowTable += "<td><input type='hidden' name='competencyIds' value='" + competencyId + "' />" + competencyName + "</td>";
                    rowTable += "<td ><input type='hidden' name='otherSpecifications' value='" + otherSpecifications + "' />" + otherSpecifications + "</td>";
                    rowTable += "<td>";
                    rowTable += "<span class='delete-action'><a style='cursor: pointer;' class='red icon-trash ' onclick='deleteRow(" + index + ")' title='<g:message code='default.button.delete.label'/>'></a> </span>";//delete
                    rowTable += "</td></tr>";
                    $('#previousWorkTable').append(rowTable)
                }

                $("#fromAge").val(json.data.fromAge);
                var newOption = new Option(json.data.fromAge, true);
                $('#fromAge').append(newOption);
                $('#fromAge').trigger('change');

                $("#toAge").val(json.data.toAge);
                var newOption = new Option(json.data.toAge, true);
                $('#toAge').append(newOption);
                $('#toAge').trigger('change');

                $("#fromTall").val(json.data.fromTall);
                var newOption = new Option(json.data.fromTall, true);
                $('#fromTall').append(newOption);
                $('#fromTall').trigger('change');

                $("#toTall").val(json.data.toTall);
                var newOption = new Option(json.data.toTall, true);
                $('#toTall').append(newOption);
                $('#toTall').trigger('change');

                $("#fromWeight").val(json.data.fromWeight);
                var newOption = new Option(json.data.fromWeight, true);
                $('#fromWeight').append(newOption);
                $('#fromWeight').trigger('change');

                $("#toWeight").val(json.data.toWeight);
                var newOption = new Option(json.data.toWeight, true);
                $('#toWeight').append(newOption);
                $('#toWeight').trigger('change');

                $("#maritalStatusId").val(json.data.maritalStatusId);
                var newOption = new Option(json.data.maritalStatusName, json.data.maritalStatusId, true, true);
                if (newOption) {
                    $('#maritalStatusId').append(newOption);
                    $('#maritalStatusId').trigger('change');
                }

                $("#note").val(json.data.note);
                var newOption = new Option(json.data.note, true);
                $('#note').append(newOption);
                $('#note').trigger('change');


                $("#modal-form").modal('hide');
            }
        });
    }

    $(document).ready(function () {
        $('#modal-form').on('hide.bs.modal', function () {
            removeCloseBtn();
        });


        $('#application-modal-main-content').on('shown.bs.modal', function () {
            var id = $("#job").val();
            var name = $("#job option:selected").text();
            $("#name").val(id);
            gui.dataTable.initialize($('#application-modal-main-content'));
            gui.modal.initialize($('#application-modal-main-content'));
        });

    });
    $(document).ready(function () {
        $('#modalFormForJobRequisition').on('hide.bs.modal', function () {
            removeCloseBtn();
        });
    });
    $(document).ready(function () {
        $('#modalFormForJobRequisition').on('show.bs.modal', function () {
            removeCloseBtn();
            _dataTables['jobRequisitionTableForNumberOfPosition'].draw();
        });
    });


    $('#modal-form').on('shown.bs.modal', function () {
        $("#jobRequisitionIdTitle").val($("#jobRequisitionId").val());
        _dataTables['jobRequisitionTable1'].draw();
    });


    $("#job").on("select2:close", function (e) {
        if ($("#job").val() > 0) {
            $.ajax({
                url: '${createLink(controller: 'jobRequisition',action: 'getJobInformation')}',
                type: 'POST',
                data: {id: $("#job").val()},
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    var resetNewOption = new Option('', '', true, true);
                    $('#educationDegrees').text(resetNewOption);
                    $('#educationDegrees').trigger('change');
                    /* to set education degrees*/
                    if (json.job.transientData.educationDegreeMapList) {
                        $("#educationDegrees").val(json.job.transientData.educationDegreeMapList);
                        for (indx = 0; indx < json.job.transientData.educationDegreeMapList.length; indx++) {
                            var newOption = new Option(json.job.transientData.educationDegreeMapList[indx][1], json.job.transientData.educationDegreeMapList[indx][0], true, true);
                            $('#educationDegrees').append(newOption);
                            $('#educationDegrees').trigger('change');
                            educationDegreeList.push(json.job.transientData.educationDegreeMapList[indx][1])
                        }
                    }
                    removeCloseBtnFromEducationDegree();


                    /* to set education Majors*/
                    $('#educationMajors').text(resetNewOption);
                    $('#educationMajors').trigger('change');
                    if (json.job.transientData.educationMajorMapList) {
                        $("#educationMajors").val(json.job.transientData.educationMajorMapList);
                        for (indx = 0; indx < json.job.transientData.educationMajorMapList.length; indx++) {
                            var newOption = new Option(json.job.transientData.educationMajorMapList[indx][1], json.job.transientData.educationMajorMapList[indx][0], true, true);
                            $('#educationMajors').append(newOption);
                            $('#educationMajors').trigger('change');
                            educationMajorList.push(json.job.transientData.educationMajorMapList[indx][1])
                        }
                    }
                    removeCloseBtnFromEducationMajor();


                    var $el = $("#inspectionCategories");
                    $el.text(resetNewOption);
                    $el.trigger('change');
                    /* to set inspection categories*/
                    if (json.job.transientData.inspectionCategoryMapList) {
                        $("#inspectionCategories").val(json.job.transientData.inspectionCategoryMapList);

                        var newOption = new Option("", "", true, true);
                        $el.text(newOption);
                        $el.trigger('change');

                        for (indx = 0; indx < json.job.transientData.inspectionCategoryMapList.length; indx++) {
                            var id = json.job.transientData.inspectionCategoryMapList[indx][0];
                            var text = json.job.transientData.inspectionCategoryMapList[indx][1] + " (إلزامي) ";
                            var newOption = new Option(text, id, true, true);
                            mandatoryInspection.push(text);
                            $el.append(newOption);
                            $el.trigger('change');

                        }
                    }
                    /*to set mandatory inspection */
                    if (json.inspections.length > 0) {
                        for (indx = 0; indx < json.inspections.length; indx++) {
                            var id = json.inspections[indx].id;
                            var text = json.inspections[indx].text + " (إلزامي) ";
                            var newOption = new Option(text, id, true, true);
                            mandatoryInspection.push(text);
                            $el.append(newOption);
                            $el.trigger('change');
                        }
                    }


                    /*to set from age to age */
                    $('#fromAge').text(resetNewOption);
                    $('#fromAge').trigger('change');
                    $("#fromAge").val(json.job.fromAge);
                    var newOption = new Option(json.job.fromAge, true);
                    $('#fromAge').append(newOption);
                    $('#fromAge').trigger('change');


                    $('#toAge').text(resetNewOption);
                    $('#toAge').trigger('change');
                    $("#toAge").val(json.job.toAge);
                    var newOption = new Option(json.job.toAge, true);
                    $('#toAge').append(newOption);
                    $('#toAge').trigger('change');


                    /*to set from weight to weight*/
                    $('#fromWeight').text(resetNewOption);
                    $('#fromWeight').trigger('change');
                    $("#fromWeight").val(json.job.fromWeight);
                    var newOption = new Option(json.job.fromWeight, true);
                    $('#fromWeight').append(newOption);
                    $('#fromWeight').trigger('change');

                    $('#toWeight').text(resetNewOption);
                    $('#toWeight').trigger('change');
                    $("#toWeight").val(json.job.toWeight);
                    var newOption = new Option(json.job.toWeight, true);
                    $('#toWeight').append(newOption);
                    $('#toWeight').trigger('change');


                    removeCloseBtn();
                    guiLoading.hide();
                }
            });
        }
    });

    $("#inspectionCategories").on("select2:close", function (e) {
        removeCloseBtn();
    });


    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'vacancy',action: 'list')}";
        }
    }


</script>