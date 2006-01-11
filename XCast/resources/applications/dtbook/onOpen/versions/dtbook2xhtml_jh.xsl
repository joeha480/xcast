<?xml version="1.0" encoding="UTF-8"?>
<!-- 
		* DTBook2XHTML *
		  Joel Håkansson, TPB
		  Version 2005-06-10 09:43

		  Utan länkade noter och notreferenser
  -->
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="dtb">
<xsl:output omit-xml-declaration="yes" indent="yes" encoding="UTF-8"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
     doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

<xsl:template match="dtb:dtbook">
<xsl:variable name="lang">
<xsl:choose>
	<xsl:when test="@xml:lang!=''"><xsl:value-of select="@xml:lang"/></xsl:when>
	<xsl:otherwise>en</xsl:otherwise>
</xsl:choose>
</xsl:variable>
<html xml:lang="{$lang}" lang="{$lang}">
  <xsl:apply-templates/>
</html>
</xsl:template>

<xsl:template match="dtb:head">
  <head>
    <xsl:apply-templates/>
    <xsl:if test="count(dtb:title)=0"><title><xsl:value-of select="dtb:meta[@name='dc:title']/@content"/></title></xsl:if>
    <link rel="stylesheet" type="text/css" href="fackbok.css" />
  </head>
</xsl:template>

<xsl:template match="dtb:book">
 <body>
   <xsl:apply-templates select="dtb:frontmatter"/>
   <xsl:apply-templates select="dtb:bodymatter"/>
   <xsl:apply-templates select="dtb:rearmatter"/>
 </body>
</xsl:template>

<xsl:template name="pagenum">
  <span>
    <xsl:choose>
      <xsl:when test="@class='front'">
        <xsl:attribute name="class">page-front</xsl:attribute>
      </xsl:when>
      <xsl:when test="@class='special'">
        <xsl:attribute name="class">page-special</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="class">page-normal</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:value-of select="."/>
  </span>
</xsl:template>

<xsl:template match="dtb:pagenum">
	<xsl:call-template name="pagenum"/>
</xsl:template>

<!-- if a pagenum is inside a list, make it a list-item -->
<xsl:template match="dtb:pagenum[parent::dtb:list]">
	<li><xsl:call-template name="pagenum"/></li>
</xsl:template>

<xsl:template match="dtb:doctitle">
  <h1 class="title"><xsl:apply-templates /></h1>
</xsl:template>

<xsl:template match="dtb:imggroup">
  <div class="group"><xsl:apply-templates/></div>
</xsl:template>

<xsl:template match="dtb:prodnote">
 <div class="optional-prodnote"><xsl:apply-templates/></div>
</xsl:template>

<xsl:template match="dtb:img">
  <img src="{@src}" alt="{@alt}"/>
</xsl:template>

<xsl:template match="br">
  <br /> 
</xsl:template>

<xsl:template match="dtb:th|dtb:td">
  <xsl:element name="{name()}">
    <xsl:for-each select="@rowspan"><xsl:attribute name="rowspan"><xsl:value-of select="."/></xsl:attribute></xsl:for-each>
    <xsl:for-each select="@colspan"><xsl:attribute name="colspan"><xsl:value-of select="."/></xsl:attribute></xsl:for-each>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<!-- div class -->
<xsl:template match="dtb:frontmatter|dtb:bodymatter|dtb:rearmatter|
dtb:sidebar|dtb:level1|dtb:level2|dtb:level3|dtb:level4|dtb:level5|dtb:level6|
dtb:author|dtb:docauthor|dtb:caption">
  <div class="{name()}"><xsl:apply-templates/></div>
</xsl:template>

<!-- element name -->
<xsl:template match="dtb:h1|dtb:h2|dtb:h3|dtb:h4|dtb:h5|dtb:h6|dtb:li|dtb:table|dtb:tr|dtb:blockquote|dtb:p|dtb:em|dtb:strong|dtb:title|dtb:caption[parent::dtb:table]">
  <xsl:element name="{name()}"><xsl:apply-templates/></xsl:element>
</xsl:template>

<!-- span class -->
<xsl:template match="dtb:hd|dtb:lic">
  <span class="{name()}"><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match="dtb:noteref">
  <span class="noteref" id="lb{@idref}"><!--<a href="#{@idref}">--><xsl:apply-templates/><!--</a>--></span>
</xsl:template>

<xsl:template match="dtb:note">
  <div class="notebody" id="{@id}"><!--<a href="#lb{@id}">--><xsl:apply-templates/><!--</a>--></div>
</xsl:template>

<xsl:template match="dtb:list">
  <xsl:choose>
    <xsl:when test="@type='ol'"><ol><xsl:apply-templates/></ol></xsl:when>
    <xsl:when test="@type='pl'"><ul class="pl"><xsl:apply-templates/></ul></xsl:when>
    <xsl:otherwise><ul><xsl:apply-templates/></ul></xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>