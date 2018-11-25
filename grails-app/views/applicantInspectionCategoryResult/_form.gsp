<g:if test="${params.action == 'createInLine'}">
    <el:hiddenField name="applicant.id" id="applicantId" value="${params['ownerPerson.id']}"/>
    <el:formGroup>
        <el:autocomplete optionKey="id"
                         optionValue="name" size="8"
                         class=" isRequired" controller="applicant"
                         action="getInspectionCategoryByApplicant" name="inspectionCategory.id"
                         id="inspectionCategoryId"
                         label="${message(code: 'applicantInspectionCategoryResult.inspectionCategory.label', default: 'inspectionCategory')}"
                         values="${[[inspectionCategory?.id, inspectionCategory?.descriptionInfo?.localName]]}"
                         paramsGenerateFunction="applicantParam"
                         onchange="getInspection();"/>
    </el:formGroup>


    <script>

        function getInspection() {
            $('.alert.page').html("");
            document.getElementById("inspectionCategoryDiv").innerHTML = " ";
            if ($("#inspectionCategoryId").val()) {

                $.ajax({
                    url: "${createLink(controller: 'applicant',action: 'getInspectionCategory')}",
                    data: {
                        "inspectionCategoryId": $("#inspectionCategoryId").val()
                    },
                    type: "POST",
                    dataType: "html",
                    success: function (data) {

                        document.getElementById("inspectionCategoryDiv").innerHTML = data;
                        gui.initAll.init($('#inspectionCategoryDiv'));
                    },
                    error: function (xhr, status) {
                    }
                });


            }
        }


        function applicantParam() {
            return {
                'applicant.id': $("#applicantId").val()
            }

        }

    </script>

</g:if>


<g:else>
    <script>
        $(document).ready(function () {
            $('.alert.page').html("");
            if ($("#encodedId").val()) {
                $.ajax({
                    url: "${createLink(controller: 'applicant',action: 'getInspectionCategory')}",
                    data: {
                        "encodedId": $("#encodedId").val()
                    },
                    type: "GET",
                    dataType: "html",
                    success: function (data) {

                        document.getElementById("inspectionCategoryDiv").innerHTML = data;
                        gui.initAll.init($('#inspectionCategoryDiv'));
                    },
                    error: function (xhr, status) {
                    }
                });
            }

        })

    </script>

</g:else>


<div id="inspectionCategoryDiv"></div>




