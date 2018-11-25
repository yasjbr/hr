<div style="padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">
        ${message(code: 'province.info.label')}</h4> <hr/></div>


<g:render template="/DescriptionInfo/wrapper" model="[bean: province?.descriptionInfo]"/>
<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'province.note.label', default: 'note')}"
                 value="${province?.note}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8" class=" isRequired"
                  label="${message(code: 'province.universalCode.label', default: 'universalCode')}"
                  value="${province?.universalCode}"/>
</el:formGroup>



<div>
    <div class="col-md-12">
        <table width="100%">
            <tr>
                <td style="width: 100%;">
                    <h4 class=" smaller lighter blue">${g.message(code: "province.locationInfo.label")}</h4>
                </td>
                <td width="160px" align="left">
                    %{--add edit button to select new  location --}%
                    <el:modalLink
                            link="${createLink(controller: 'province', action: 'locationModal')}"
                            preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                            label="${message(code: 'province.selectLocation.label')}">
                    </el:modalLink>
                </td>
            </tr>
        </table>
        <hr style="margin-top: 0px;margin-bottom: 9px;"/>
    </div>

    <el:formGroup id="locationDiv">
        <div class="col-md-12">
        %{--represent location in formal way--}%
            <lay:table styleNumber="1" id="locationTable">
                <lay:tableHead title="${message(code: 'province.location.country.label')}"/>
                <lay:tableHead title="${message(code: 'province.location.region.label')}"/>
                <lay:tableHead title="${message(code: 'default.actions.label')}"/>
                <rowElement>
                    <g:each in="${province?.transientData?.locationDTOList?.sort { it.country?.id }}" var="location"
                            status="index">
                        <tr id="row-${index + 1}">
                            <td class='center'><el:hiddenField name="countryIdList" value="${location?.country?.id}"/>
                                ${location?.country?.descriptionInfo?.localName}</td>

                            <td class='center'><el:hiddenField name="${index}-${location?.country?.id}-governorateId"
                                                               value="${location?.governorate?.id}"/>
                                ${location?.governorate?.descriptionInfo?.localName}</td>

                            <td class='center'>
                                <span class='delete-action'>
                                    <a style='cursor: pointer;'
                                       class='red icon-trash '
                                       onclick="deleteRow('${index + 1}');"
                                       title='<g:message code='default.button.delete.label'/>'>

                                    </a>
                                </span>
                            </td>
                        </tr>

                    </g:each>
                </rowElement>
            </lay:table>
        </div>
    </el:formGroup>
</div>

<br/>

<script>

    var index = "${province?.id?province?.provinceLocations?.size()+1:0}";

    function governorateParams() {
        var countryId = $('#countryId').val();
        return {'country.id': countryId}
    }


    $("#countryId").on("select2:close", function (e) {
        resetGovernorate();
    });

    function resetGovernorate() {
        gui.autocomplete.clear("governorateId");
    }

    function insertLocationIntoLocationTable() {
        var countryId = $("#countryId").val();
        var countryName = $("#countryId option:selected").text();
        var governorateId = $("#governorateId").val();
        var governorateName = $("#governorateId option:selected").text();
        var rowTable = "";

        if (countryId && countryName) {

            rowTable = "<tr id='row-" + index + "' class='center' >";
            rowTable += "<td style='text-align: center;'> <input type='hidden' name='countryIdList' value='" + countryId + "' />" + countryName + "</td>";
            rowTable += "<td style='text-align: center;'> <input type='hidden' name='" + index + "-" + countryId + "-governorateId' value='" + governorateId + "' />" + governorateName + "</td>";
            rowTable += "<td style='text-align: center;'>" +
                "  <span class='delete-action'>" +
                "<a style = 'cursor: pointer;'" +
                "class = 'red icon-trash'" +
                "onclick='deleteRow(" + index +
                ");'" +
                "></a></span></td>";
            rowTable += "</tr>";

            $("#locationTable").append(rowTable);
            index++;
            gui.formValidatable.resetForm('locationModal');
            showInfo("${message(code: 'province.success.add.message')}");
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

    function deleteRow(index) {
        $("#row-" + index).remove();
    }
</script>