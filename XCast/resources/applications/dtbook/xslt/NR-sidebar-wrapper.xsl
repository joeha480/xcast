<?xml version="1.0" encoding="UTF-8"?>
<!--
		*** Sidebar-wrapper ***
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!-- Behandla sidebars som inte har några andra föregående syskon -->
<xsl:template match="*[position()=1 and self::sidebar]">
	<xsl:variable name="set" select=".|following-sibling::sidebar[count(preceding-sibling::*[not(self::sidebar)])=0]"/>
	<xsl:call-template name="process"><xsl:with-param name="set" select="$set"/></xsl:call-template>
</xsl:template>

<!-- Behandla inte övriga sidebars här -->
<xsl:template match="sidebar"/>

<!-- Behandla den nod som föregår en sidebar speciellt -->
<xsl:template match="*[not(self::sidebar) and following-sibling::*[1][self::sidebar]]">
	<xsl:call-template name="copy"/>
	<xsl:variable name="set" select="following-sibling::sidebar[generate-id(preceding-sibling::*[not(self::sidebar)][1])=generate-id(current())]"/>
	<xsl:call-template name="process"><xsl:with-param name="set" select="$set"/></xsl:call-template>
</xsl:template>

<xsl:template name="process">
	<xsl:param name="set"/>
	<sidebar render="optional">
		<xsl:choose>
			<xsl:when test="count($set)&gt;1">
				<hd><xsl:apply-templates select="$set[1]/node()"/></hd>
				<xsl:for-each select="$set[position()&gt;1]">
					<p><xsl:apply-templates/></p>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<p><xsl:apply-templates select="$set[1]/node()"/></p>
			</xsl:otherwise>
		</xsl:choose>
	</sidebar>
</xsl:template>

<!-- platta hd och p som redan finns inuti sidebars -->
<xsl:template match="hd[parent::sidebar]|p[parent::sidebar]">
  <xsl:apply-templates/>
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
