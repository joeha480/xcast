<!--
	WordML2dtBook2

	Known issues: Lists dont work
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:w="http://schemas.microsoft.com/office/word/2003/wordml"
	xmlns:v="urn:schemas-microsoft-com:vml"
	xmlns:w10="urn:schemas-microsoft-com:office:word"
	xmlns:sl="http://schemas.microsoft.com/schemaLibrary/2003/core"
	xmlns:aml="http://schemas.microsoft.com/aml/2001/core"
	xmlns:wx="http://schemas.microsoft.com/office/word/2003/auxHint"
	xmlns:o="urn:schemas-microsoft-com:office:office"
	xmlns:dt="uuid:C2F41010-65B3-11d1-A29F-00AA00C14882"
	xmlns:st1="urn:schemas-microsoft-com:office:smarttags"
	xmlns="http://www.daisy.org/z3986/2005/dtbook/"
	exclude-result-prefixes="w v w10 sl aml wx o dt st1">
	
<xsl:output method="xml" indent="yes" encoding="UTF-8" 
	doctype-public="-//NISO//DTD dtbook v1.2.1//EN"
	doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-2.dtd"/>

<xsl:template match="w:wordDocument">
	<xsl:processing-instruction name="xml-stylesheet">type="text/xsl" href="dtbook2xhtml.xsl"</xsl:processing-instruction>
	<dtbook>
		<head/>
		<book>
			<bodymatter>
				<xsl:apply-templates/>
			</bodymatter>
		</book>
	</dtbook>
</xsl:template>

<xsl:template match="w:body">
	<xsl:apply-templates/>
</xsl:template>


<xsl:template match="wx:sub-section">
	<xsl:variable name="ename" select="concat('level', count(ancestor-or-self::wx:sub-section))"/>
	<xsl:element name="{$ename}">
		<xsl:apply-templates/>
	</xsl:element>
</xsl:template>

<xsl:template match="wx:sect">
	<xsl:if test="count(*[not(self::wx:sub-section)])&gt;0">
		<level1>
			<xsl:apply-templates select="*[not(self::wx:sub-section)]"/>
		</level1>
	</xsl:if>
	<xsl:apply-templates select="*[self::wx:sub-section]"/>
</xsl:template>

<xsl:template match="w:p[w:pPr/w:pStyle/@w:val='TPB-Metadata']">
	<xsl:comment><xsl:value-of select="."/></xsl:comment>
</xsl:template>

<xsl:template match="w:p[w:pPr/w:listPr]" mode="processList"> <!-- Process list -->
	<xsl:variable name="level" select="w:pPr/w:listPr/w:ilvl/@w:val"/>
	<xsl:variable name="id" select="generate-id(.)"/>
	<list type="pl">
		<xsl:for-each select=".|following-sibling::w:p[
			generate-id(preceding-sibling::w:p[w:pPr/w:listPr/w:ilvl/@w:val=$level][last()])=$id]  
			[w:pPr/w:listPr/w:ilvl/@w:val=$level]">
			<li>
				<xsl:value-of select="w:pPr/w:listPr/wx:t/@wx:val"/>
				<xsl:text> </xsl:text>
				<xsl:apply-templates select="w:r"/>
				<xsl:apply-templates select="following-sibling::w:p[
				generate-id(preceding-sibling::w:p[w:pPr/w:listPr/w:ilvl/@w:val=$level][1])=$id]
				[w:pPr/w:listPr/w:ilvl/@w:val=$level+1]" mode="processList"/>
			</li>
		</xsl:for-each>
	</list>
</xsl:template>

<!-- Begin list -->
<xsl:template match="w:p[w:pPr/w:listPr][count(preceding-sibling::w:p[1][w:pPr/w:listPr])=0]" priority="10"> 
	<xsl:apply-templates select="." mode="processList"/>
</xsl:template>

<xsl:template match="w:p[w:pPr/w:listPr]" priority="5"/>

<xsl:template match="w:p">
	<xsl:variable name="ename">
		<xsl:choose>
			<xsl:when test="w:pPr/w:pStyle/@w:val='Rubrik1'">h1</xsl:when>
			<xsl:when test="w:pPr/w:pStyle/@w:val='Rubrik2'">h2</xsl:when>
			<xsl:when test="w:pPr/w:pStyle/@w:val='Rubrik3'">h3</xsl:when>
			<xsl:when test="w:pPr/w:pStyle/@w:val='Rubrik4'">h4</xsl:when>
			<xsl:when test="w:pPr/w:pStyle/@w:val='Rubrik5'">h5</xsl:when>
			<xsl:when test="w:pPr/w:pStyle/@w:val='Rubrik6'">h6</xsl:when>
			<!-- <xsl:when test="w:pPr/w:pStyle/@w:val!=''"><xsl:value-of select="w:pPr/w:pStyle/@w:val"/></xsl:when> -->
			<xsl:otherwise>p</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:element name="{$ename}">
		<xsl:apply-templates select="w:r"/>
	</xsl:element>
</xsl:template>

<xsl:template match="w:r">
	<xsl:choose>
		<xsl:when test="w:rPr/w:rStyle/@w:val='Betoning'"><em><xsl:value-of select="."/></em></xsl:when>
		<xsl:when test="w:rPr/w:rStyle/@w:val='Stark'"><strong><xsl:value-of select="."/></strong></xsl:when>
		<xsl:when test="w:rPr/w:rStyle/@w:val='Sidnummer'"><pagenum id="p-{.}"><xsl:value-of select="."/></pagenum></xsl:when>
		<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="w:t">
	<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="w:br">
	<br/>
</xsl:template>

<xsl:template match="w:tbl">
	<table>
		<xsl:apply-templates/>
	</table>
</xsl:template>

<xsl:template match="w:tr">
	<tr>
		<xsl:apply-templates/>
	</tr>
</xsl:template>

<xsl:template match="w:tc">
	<td>
		<xsl:apply-templates/>
	</td>
</xsl:template>

<xsl:template match="*"/>

<xsl:template name="copy">
  <xsl:element name="{name()}">
  	<xsl:for-each select="@*">
  	  <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
  	</xsl:for-each>
  	<xsl:apply-templates/>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>