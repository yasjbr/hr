
<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personHealthHistory?.person,
                                             isDisabled:isPersonDisabled]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="diseaseType" action="autocomplete" name="diseaseType.id" label="${message(code:'personHealthHistory.diseaseType.label',default:'diseaseType')}" values="${[[personHealthHistory?.diseaseType?.id,personHealthHistory?.diseaseType?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="diseaseName" size="8"  class=" isRequired" label="${message(code:'personHealthHistory.diseaseName.label',default:'diseaseName')}" value="${personHealthHistory?.diseaseName}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="affictionDate"  size="8" class=" isRequired" label="${message(code:'personHealthHistory.affictionDate.label',default:'affictionDate')}" value="${personHealthHistory?.affictionDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="description" size="8"  class="" label="${message(code:'personHealthHistory.description.label',default:'description')}" value="${personHealthHistory?.description}"/>
</el:formGroup>

<lay:wall title="${g.message(code: 'personHealthHistory.affictionLocation.label')}">
    <g:render template="/pcore/location/wrapper"
              model="[location                  : personHealthHistory?.affictionLocation,
                      isRegionRequired          : false,
                      isCountryRequired         : false,
                      showCountryWithOutRequired: true,
                      hiddenDetails             : true,
                      size                      : 8,
                      fieldName                 :'affictionLocation',
                      isDistrictRequired        : false]"/>
    <el:formGroup>
        <el:textArea name="unstructuredAffictionLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" value="${personHealthHistory?.unstructuredAffictionLocation}"/>
    </el:formGroup>
</lay:wall>