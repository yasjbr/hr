
<g:render template="/DescriptionInfo/wrapper" model="[bean:evaluationSection?.descriptionInfo]" />
<el:formGroup>
    <el:integerField name="index" size="8"  class=" isRequired isNumber" label="${message(code:'evaluationSection.index.label',default:'index')}" value="${evaluationSection?.index}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="evaluationTemplate" action="autocomplete" name="evaluationTemplate.id" label="${message(code:'evaluationSection.evaluationTemplate.label',default:'evaluationTemplate')}" values="${[[evaluationSection?.evaluationTemplate?.id,evaluationSection?.evaluationTemplate?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="hint" size="8"  class="" label="${message(code:'evaluationSection.hint.label',default:'hint')}" value="${evaluationSection?.hint}"/>
</el:formGroup>




%{--<div class="col-md-12">--}%
    %{--<table width="100%">--}%
        %{--<tr>--}%
            %{--<td style="width: 100%;">--}%
                %{--<h4 class=" smaller lighter blue">${message(code: 'evaluationItem.label')}</h4>--}%
            %{--</td>--}%
            %{--<td width="160px" align="left">--}%
                %{--<el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"--}%
                              %{--link="${createLink(controller: 'evaluationItem', action: 'createItemModal')}"--}%
                              %{--label="${message(code: 'evaluationSection.addItem.label')}">--}%
                    %{--<i class="ace-icon fa"></i>--}%
                %{--</el:modalLink>--}%
            %{--</td>--}%
        %{--</tr>--}%
    %{--</table>--}%
    %{--<hr style="margin-top: 0px;margin-bottom: 9px;"/>--}%
%{--</div>--}%


%{--<el:formGroup>--}%
    %{--<div class="col-md-12" id="tableDiv">--}%
        %{--<lay:table styleNumber="1" id="evaluationItemTable">--}%
            %{--<lay:tableHead title="${message(code: 'evaluationItem.descriptionInfo.localName.label')}"/>--}%
            %{--<lay:tableHead title="${message(code: 'evaluationItem.index.label')}"/>--}%
            %{--<lay:tableHead title="${message(code: 'evaluationItem.maxMark.label')}"/>--}%
            %{--<lay:tableHead title="${message(code: 'evaluationItem.universalCode.label')}"/>--}%

            %{--<g:each in="${evaluationSection?.evaluationItems?.sort { it?.index }}" var="item"--}%
                    %{--status="index">--}%
                %{--<rowElement>--}%
                    %{--<tr id="row-${index + 1}" class='center'>--}%
                        %{--<td class='center'>${item?.descriptionInfo?.localName}</td>--}%
                        %{--<td class='center'>${item?.index}</td>--}%
                        %{--<td class='center'>${item?.maxMark}</td>--}%
                        %{--<td class='center'>${item?.universalCode}</td>--}%
                        %{--<td class='center'>--}%
                            %{--<input type='hidden' name='item_localName' id='localName-${index + 1}'--}%
                                   %{--value='${item?.descriptionInfo?.localName}'/>--}%

                            %{--<input type='hidden' name='item_index' id='index-${index + 1}'--}%
                                   %{--value='${item?.index}'/>--}%

                            %{--<input type='hidden' name='item_maxMark' id='maxMark-${index + 1}'--}%
                                   %{--value='${item?.maxMark}'/>--}%
                            %{--<input type='hidden' name='item_universalCode' id='universalCode-${index + 1}'--}%
                                   %{--value='${item?.universalCode}'/>--}%
                        %{--</td>--}%
                    %{--</tr>--}%
                %{--</rowElement>--}%
            %{--</g:each>--}%

            %{--<g:if test="${!evaluationSection?.evaluationItems}">--}%
                %{--<rowElement>--}%
                    %{--<tr id="row-0" class='center'>--}%
                        %{--<td class='center'></td>--}%
                        %{--<td class='center'></td>--}%
                        %{--<td class='center'></td>--}%
                        %{--<td class='center'></td>--}%
                        %{--<td class='center'></td>--}%
                    %{--</tr>--}%
                %{--</rowElement>--}%
            %{--</g:if>--}%

        %{--</lay:table>--}%
    %{--</div>--}%
%{--</el:formGroup>--}%


%{--<script>--}%

    %{--var globalIndex = ${(evaluationSection?.id)?(evaluationSection?.evaluationItems?.size()+1):1};--}%

    %{--function addNewItem() {--}%
        %{--$('.alert.modalPage').html("");--}%
        %{--var localName = $("#itemDescription").val() ? $("#itemDescription").val() : "";--}%
        %{--var index = $("#itemIndex").val() ? $("#itemIndex").val() : "";--}%
        %{--var maxMark = $("#itemMaxMark").val() ? $("#itemMaxMark").val() : "";--}%
        %{--var universalCode = $("#itemUniversalCode").val() ? $("#itemUniversalCode").val() : "";--}%
        %{--if (index == "" || maxMark == "" || localName == "") {--}%
            %{--showError("${message(code:'jobRequisition.error1.label')}");--}%
        %{--} else {--}%
            %{--var rowTable = "";--}%
            %{--rowTable += "<tr id='row-" + globalIndex + "' class='center' >";--}%
            %{--rowTable += "<td class='center'>" + localName + "</td>";--}%
            %{--rowTable += "<td class='center'>" + index + "</td>";--}%
            %{--rowTable += "<td class='center'>" + maxMark + "</td>";--}%
            %{--rowTable += "<td class='center'>" + universalCode + "</td>";--}%
            %{--rowTable += "<td class='center'>";--}%
            %{--rowTable += "<input type='hidden' name='item_localName'  id='localName-" + globalIndex + "' value='" + localName + "'>";--}%
            %{--rowTable += "<input type='hidden' name='item_index'  id='index-" + globalIndex + "' value='" + index + "'>";--}%
            %{--rowTable += "<input type='hidden' name='item_maxMark'  id='maxMark-" + globalIndex + "' value='" + maxMark + "'>";--}%
            %{--rowTable += "<input type='hidden' name='item_universalCode'  id='universalCode-" + globalIndex + "' value='" + universalCode + "'>";--}%
            %{--rowTable += "</td>" + "</tr>";--}%

            %{--$("#evaluationItemTable").append(rowTable);--}%

            %{--showInfo("${message(code:'jobRequisition.previousWork.modal.add.success')}");--}%
            %{--globalIndex++;--}%
            %{--resetForm();--}%
        %{--}--}%
    %{--}--}%

    %{--function resetForm() {--}%
        %{--$("#itemDescription").val("");--}%
        %{--$("#itemIndex").val("");--}%
        %{--$("#itemMaxMark").val("");--}%
        %{--$("#itemUniversalCode").val("");--}%
    %{--}--}%

    %{--function showInfo(infoMessage) {--}%
        %{--var msg = "<div class='alert alert-block alert-success'>" +--}%
            %{--"<button data-dismiss='alert' class='close' type='button'>" +--}%
            %{--"<i class='ace-icon fa fa-check'>" +--}%
            %{--"</i>" +--}%
            %{--"</button>" +--}%
            %{--"<ul>" +--}%
            %{--"<li>" + infoMessage + "</li> " +--}%
            %{--"</ul>" +--}%
            %{--"</div>";--}%
        %{--$('.alert.modalPage').html("");--}%
        %{--$('.alert.modalPage').html(msg);--}%
    %{--}--}%

    %{--function showError(errorMessage) {--}%
        %{--var msg = "<div class='alert alert-block alert-danger'>" +--}%
            %{--"<button data-dismiss='alert' class='close' type='button'>" +--}%
            %{--"<i class='ace-icon fa fa-times'>" +--}%
            %{--"</i>" +--}%
            %{--"</button>" +--}%
            %{--"<ul>" +--}%
            %{--"<li>" + errorMessage + "</li> " +--}%
            %{--"</ul>" +--}%
            %{--"</div>";--}%
        %{--$('.alert.modalPage').html(msg);--}%
    %{--}--}%


%{--</script>--}%
