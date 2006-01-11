<?xml version="1.0" encoding="UTF-8"?>
<!--<html>
	<body>
		<p><b>Wrapper-fix:</b></p>
		<p>Wrappar noder av omärkt text som<br>
           är barn till <b>levelx</b>. Den första<br>
           omärkta noden wrappas med <b>hx</b><br>
           och följande med <b>p</b></p>
	</body>
    </html>
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-22 08:32
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>

	<xsl:template match="/">
	    <xsl:comment>Wrapper fix, version 2005-09-22 08:32</xsl:comment>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- If a text-node contains nothing but whitespace, it is removed -->
	<xsl:template match="text()[normalize-space()='']" priority="10"/>

	<xsl:template match="text()[position()=1 and parent::level1 and not(parent::*/h1)]" priority="5">
		<h1><xsl:value-of select="normalize-space()"/></h1>
	</xsl:template>

	<xsl:template match="text()[position()=1 and parent::level2 and not(parent::*/h2)]" priority="5">
		<h2><xsl:value-of select="normalize-space()"/></h2>
	</xsl:template>

	<xsl:template match="text()[position()=1 and parent::level3 and not(parent::*/h3)]" priority="5">
		<h3><xsl:value-of select="normalize-space()"/></h3>
	</xsl:template>

	<xsl:template match="text()[position()=1 and parent::level4 and not(parent::*/h4)]" priority="5">
		<h4><xsl:value-of select="normalize-space()"/></h4>
	</xsl:template>

	<xsl:template match="text()[position()=1 and parent::level5 and not(parent::*/h5)]" priority="5">
		<h5><xsl:value-of select="normalize-space()"/></h5>
	</xsl:template>
	
	<xsl:template match="text()[position()=1 and parent::level6 and not(parent::*/h6)]" priority="5">
		<h6><xsl:value-of select="normalize-space()"/></h6>
	</xsl:template>
	
	<xsl:template match="text()[parent::level1 or parent::level2 or parent::level3 or parent::level4 or parent::level5 or parent::level6]" priority="3">
		<p><xsl:value-of select="normalize-space()"/></p>
	</xsl:template>

	<xsl:template match="*">
		<xsl:call-template name="copy"/>
	</xsl:template>
	
	<xsl:template name="copy">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
