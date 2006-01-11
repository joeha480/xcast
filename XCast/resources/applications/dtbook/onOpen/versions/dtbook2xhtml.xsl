<?xml version="1.0" encoding="UTF-8"?>
<!-- 
		* DTBook2XHTML *
		  Joel Håkansson, TPB
		  Version 2005-05-23 15:15

  -->
<xsl:stylesheet version="1.1" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dtb="http://www.loc.gov/nls/z3986/2004/dtbook/"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="dtb">
<xsl:output omit-xml-declaration="no" indent="yes"/>

<xsl:template match="dtbook">
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

<xsl:template match="head">
  <head>
    <xsl:apply-templates/>
    <link rel="stylesheet" type="text/css" href="style.css" />
  </head>
</xsl:template>

<xsl:template match="book">
 <body>
   <xsl:apply-templates select="frontmatter"/>
   <xsl:apply-templates select="bodymatter"/>
   <xsl:apply-templates select="rearmatter"/>
 </body>
</xsl:template>

<xsl:template match="pagenum">
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

<xsl:template match="doctitle">
  <h1 class="title"><xsl:apply-templates /></h1>
</xsl:template>

<xsl:template match="imggroup">
  <div class="group"><xsl:apply-templates/></div>
</xsl:template>

<xsl:template match="prodnote">
 <div class="optional-prodnote"><xsl:apply-templates/></div>
</xsl:template>

<xsl:template match="img">
  <img src="{@src}" alt="{@alt}"/>
</xsl:template>

<xsl:template match="br">
  <br /> 
</xsl:template>

<xsl:template match="th|td">
  <xsl:element name="{name()}">
    <xsl:for-each select="@rowspan"><xsl:attribute name="rowspan"><xsl:value-of select="."/></xsl:attribute></xsl:for-each>
    <xsl:for-each select="@colspan"><xsl:attribute name="colspan"><xsl:value-of select="."/></xsl:attribute></xsl:for-each>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<!-- div class -->
<xsl:template match="frontmatter|bodymatter|rearmatter|sidebar|level1|level2|level3|level4|level5|level6|author|docauthor|caption">
  <div class="{name()}"><xsl:apply-templates/></div>
</xsl:template>

<!-- element name -->
<xsl:template match="h1|h2|h3|h4|h5|h6|li|table|tr|blockquote|p|em|strong|title">
  <xsl:element name="{name()}"><xsl:apply-templates/></xsl:element>
</xsl:template>

<!-- span class -->
<xsl:template match="hd|lic">
  <span class="{name()}"><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match="noteref">
  <span class="noteref"><!--<a href="#{@idref}">--><xsl:apply-templates/><!--</a>--></span>
</xsl:template>

<xsl:template match="note">
  <div class="notebody" id="{@id}"><xsl:apply-templates/></div>
</xsl:template>

<xsl:template match="list">
  <xsl:choose>
    <xsl:when test="@type='ol'"><ol><xsl:apply-templates/></ol></xsl:when>
    <xsl:otherwise><ul><xsl:apply-templates/></ul></xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>