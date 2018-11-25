<el:formGroup>
    <el:textField class="${isSearch?"":"isRequired"}"
                  name="descriptionInfo.localName" size="${size?:"8"}"
                  label="${messageValue?:(message(code:'descriptionInfo.localName.label',default:'localName'))}"
                  value="${bean?.localName}" />
</el:formGroup>

<el:formGroup>
    <el:textField  name="descriptionInfo.latinName" size="${size?:"8"}"
                   label="${message(code:'descriptionInfo.latinName.label',default:'latinName')}"
                   value="${bean?.latinName}"/>
</el:formGroup>

%{--add if statement to exclude hebrew from search because it is not used in the search method in service --}%
<g:if test="${includeHebrow!=null?includeHebrow:!isSearch}">
    <el:formGroup>
        <el:textField  name="descriptionInfo.hebrewName" size="${size?:"8"}"
                       label="${message(code:'descriptionInfo.hebrewName.label',default:'hebrewName')}"
                       value="${bean?.hebrewName}"/>
    </el:formGroup>
</g:if>
