<script type="text/javascript">


    var mandatoryInspection = [];
    var educationDegreeList = [];
    var educationMajorList = [];
    var militaryRankList = [];

    <%
       jobRequisition?.transientData?.educationDegreeList?.eachWithIndex{value, index ->
    %>
    educationDegreeList.push("${value}");
    <%
      }
    %>
    <%
       jobRequisition?.transientData?.educationMajorList?.eachWithIndex{value, index ->
    %>
    educationMajorList.push("${value}");
    <%
      }
    %>
    <%
       jobRequisition?.transientData?.inspectionList?.eachWithIndex{value, index ->
    %>
    mandatoryInspection.push("${value}");
    <%
      }
    %>

    function afterSave() {
        $("#detailsTable tbody.workExperiences").empty();
        index = ${(jobRequisition?.id)?(jobRequisition?.requisitionWorkExperiences?.size()+1):1};
    }

    %{-- Previous Work --}%

    var index = ${(jobRequisition?.id)?(jobRequisition?.requisitionWorkExperiences?.size()+1):1};

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

    function resetForm() {
        gui.autocomplete.clear("workExperience-professionType");
        gui.autocomplete.clear("workExperience-competency");
        $('#otherSpecifications_').val("");
        $('#periodInYears_').val("");
    }


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

            if (li != null) {
                li.addClass("educationDegreeList");
                li.css("background-color", "rgba(32, 98, 213, 0.34)");
                li.find("span").remove();
            }
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


    /*to get mandatory inspection*/
    $(document).ready(function () {
        $(window).load(function () {
            $(".profile-info-name").css('border', '1px solid #f7fbff');
            var $el = $("#inspectionCategories");
            var selectionRendered = $(".inspectionCategoriesDiv").find(".select2-selection__rendered");

            $.ajax({
                url: "<g:createLink controller="JobRequisition" action="getMandatoryInspection"/>",
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

            removeCloseBtnFromEducationDegree();
            removeCloseBtnFromEducationMajor();
            removeCloseBtn();

        });

        $("#jobId").on("select2:close", function (e) {
            $("#jobDescription").prop("readonly", false);
            $('#jobDescription').val("");
            militaryRankList = null;

            if($("#jobId").val() && $("#jobId").val().trim()!=""){
                $.ajax({

                    url: '${createLink(controller: 'jobRequisition',action: 'getJobInformation')}',
                    type: 'POST',
                    data: {id: $("#jobId").val()},
                    dataType: 'json',
                    beforeSend: function (jqXHR, settings) {
                        guiLoading.show();
                    },
                    error: function (jqXHR) {
                        guiLoading.hide();
                    },
                    success: function (json) {
                        militaryRankList = json.militaryRanks;
                        var resetNewOption = new Option('', '', true, true);
                        $('#educationDegrees').text(resetNewOption);
                        $('#educationDegrees').trigger('change');
                        /* to set education degrees*/
                        if (json.job.transientData.educationDegreeMapList) {
                            $("#educationDegrees").val(json.job.transientData.educationDegreeMapList);
                            $(".hiddenEducationDegreesDiv input").remove();
                            for (indx = 0; indx < json.job.transientData.educationDegreeMapList.length; indx++) {
                                var newOption = new Option(json.job.transientData.educationDegreeMapList[indx][1], json.job.transientData.educationDegreeMapList[indx][0], true, true);
                                $('#educationDegrees').append(newOption);
                                $(".hiddenEducationDegreesDiv").append("<input type='hidden' name='educationDegrees'  id='educationDegree-" + indx + "' value='" + json.job.transientData.educationDegreeMapList[indx][0] + "'>");
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

                        /*to set from height to height*/
                        $('#fromHeight').text(resetNewOption);
                        $('#fromHeight').trigger('change');
                        $("#fromHeight").val(json.job.fromHeight);
                        var newOption = new Option(json.job.fromHeight, true);
                        $('#fromHeight').append(newOption);
                        $('#fromHeight').trigger('change');

                        $('#toHeight').text(resetNewOption);
                        $('#toHeight').trigger('change');
                        $("#toHeight").val(json.job.toHeight);
                        var newOption = new Option(json.job.toHeight, true);
                        $('#toHeight').append(newOption);
                        $('#toHeight').trigger('change');

                        if(json.job.note != null && json.job.note != ""){
                            $("#jobDescription").prop("readonly", true);
                            $('#jobDescription').val(json.job.note);
                        }
                        removeCloseBtn();
                        guiLoading.hide();
                    }
                });
            }

        });

        /*in case edit the job requisition, we want to ensure if the job was selected, then set some values.*/
        <g:if test="${jobRequisition?.job?.id}">
            $("#jobDescription").prop("readonly", false);
            $('#jobDescription').val("");
            militaryRankList = null;
            $.ajax({
                url: '${createLink(controller: 'jobRequisition',action: 'getJobInformation')}',
                type: 'POST',
                data: {id: '${jobRequisition?.job?.id}'},
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    militaryRankList = json.militaryRanks;
                    if(json.job.note != null && json.job.note != ""){
                        $("#jobDescription").prop("readonly", true);
                        $('#jobDescription').val(json.job.note);
                    }
                    guiLoading.hide();
                }
            });
        </g:if>

        $("#inspectionCategories").on("select2:close", function (e) {
            removeCloseBtn();
        });
        $("#educationDegrees").on("select2:close", function (e) {
            removeCloseBtnFromEducationDegree();
        });
        $("#educationMajors").on("select2:close", function (e) {
            removeCloseBtnFromEducationMajor();
        });


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

        $("#educationDegrees").on('select2:select', function (evt) {
            removeCloseBtnFromEducationDegree();
        });
        $("#educationDegrees").on('select2:selecting', function (evt) {
            removeCloseBtnFromEducationDegree();
        });
        $("#educationDegrees").on('change', function (evt) {
            removeCloseBtnFromEducationDegree();
        });
        $("#educationDegrees").on('select2:loaded', function (evt) {
            removeCloseBtnFromEducationDegree();
        });
        $("#educationDegrees").on('select2:removed', function (evt) {
            removeCloseBtnFromEducationDegree();
        });
        $("#educationDegrees").on('select2:open', function (evt) {
            removeCloseBtnFromEducationDegree();
        });

        $("#educationMajors").on('select2:select', function (evt) {
            removeCloseBtnFromEducationMajor();
        });
        $("#educationMajors").on('select2:selecting', function (evt) {
            removeCloseBtnFromEducationMajor();
        });
        $("#educationMajors").on('change', function (evt) {
            removeCloseBtnFromEducationMajor();
        });
        $("#educationMajors").on('select2:loaded', function (evt) {
            removeCloseBtnFromEducationMajor();
        });
        $("#educationMajors").on('select2:removed', function (evt) {
            removeCloseBtnFromEducationMajor();
        });
        $("#educationMajors").on('select2:open', function (evt) {
            removeCloseBtnFromEducationMajor();
        });

    });

    function closePreviousWorkModal() {
        $('#application-modal-main-content').modal("hide");
    }

    function addPreviousWork() {
        $('.alert.modalPage').html("");
        var professionType = $("#workExperience-professionType option:selected").text();
        var professionTypeId = $("#workExperience-professionType option:selected").val() ? $("#workExperience-professionType option:selected").val() : null
        var competency = $("#workExperience-competency option:selected").text();
        var competencyId = $("#workExperience-competency option:selected").val() ? $("#workExperience-competency option:selected").val() : null
        var periodInYears = $("#periodInYears_").val() ? $("#periodInYears_").val() : null
        var otherSpecifications = $("#otherSpecifications_").val() ? $("#otherSpecifications_").val() : ""

        if (periodInYears == null) {
            showError("${message(code:'jobRequisition.error2.label')}");
        } else if (professionTypeId == null && competencyId == null && otherSpecifications == "") {
            showError("${message(code:'jobRequisition.error1.label')}");
        } else {
            var rowTable //= "<rowElement>";
            rowTable += "<tr id='row-" + index + "' class='center' >";
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
            rowTable += "</td>" +
                    "</tr>" //+ "</rowElement>";
            //end actions
            $("#previousWorkTable").append(rowTable);
            showInfo("${message(code:'jobRequisition.previousWork.modal.add.success')}");
            index++;
            $('#row-0').remove();
            resetForm();
        }
    }

    function callBackFunction() {
        window.location.reload();
    }

    function getMilitaryRankParams() {
        return{
            "ids[]": militaryRankList
        }

    }

    function getDepartmentParams() {
        return{
            "departmentTypeList": ['${ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.DEPARTMENT}'
                                  ,'${ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.GOVERNEROTE}']
        }
    }

</script>
