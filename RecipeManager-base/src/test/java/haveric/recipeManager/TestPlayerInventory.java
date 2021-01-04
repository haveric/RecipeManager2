package haveric.recipeManager;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class TestPlayerInventory extends TestCraftInventory implements PlayerInventory, EntityEquipment {
    public TestPlayerInventory() {
        super();
        inventory = new ItemStack[40];
    }

    @Override
    public HumanEntity getHolder() {
        return null;
    }

    @Override
    public float getItemInHandDropChance() {
        return 0;
    }

    @Override
    public void setItemInHandDropChance(float chance) {

    }

    @Override
    public float getItemInMainHandDropChance() {
        return 0;
    }

    @Override
    public void setItemInMainHandDropChance(float chance) {

    }

    @Override
    public float getItemInOffHandDropChance() {
        return 0;
    }

    @Override
    public void setItemInOffHandDropChance(float chance) {

    }

    @Override
    public float getHelmetDropChance() {
        return 0;
    }

    @Override
    public void setHelmetDropChance(float chance) {

    }

    @Override
    public float getChestplateDropChance() {
        return 0;
    }

    @Override
    public void setChestplateDropChance(float chance) {

    }

    @Override
    public float getLeggingsDropChance() {
        return 0;
    }

    @Override
    public void setLeggingsDropChance(float chance) {

    }

    @Override
    public float getBootsDropChance() {
        return 0;
    }

    @Override
    public void setBootsDropChance(float chance) {

    }

    @Override
    public ItemStack[] getArmorContents() {
        return new ItemStack[0];
    }

    @Override
    public ItemStack[] getExtraContents() {
        return new ItemStack[0];
    }

    @Override
    public ItemStack getHelmet() {
        return null;
    }

    @Override
    public ItemStack getChestplate() {
        return null;
    }

    @Override
    public ItemStack getLeggings() {
        return null;
    }

    @Override
    public ItemStack getBoots() {
        return null;
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item) {

    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item, boolean silent) {

    }

    @Override
    public ItemStack getItem(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void setArmorContents(ItemStack[] items) {

    }

    @Override
    public void setExtraContents(ItemStack[] items) {

    }

    @Override
    public void setHelmet(ItemStack helmet) {

    }

    @Override
    public void setHelmet(ItemStack helmet, boolean silent) {

    }

    @Override
    public void setChestplate(ItemStack chestplate) {

    }

    @Override
    public void setChestplate(ItemStack chestplate, boolean silent) {

    }

    @Override
    public void setLeggings(ItemStack leggings) {

    }

    @Override
    public void setLeggings(ItemStack leggings, boolean silent) {

    }

    @Override
    public void setBoots(ItemStack boots) {

    }

    @Override
    public void setBoots(ItemStack boots, boolean silent) {

    }

    @Override
    public ItemStack getItemInMainHand() {
        return null;
    }

    @Override
    public void setItemInMainHand(ItemStack item) {

    }

    @Override
    public void setItemInMainHand(ItemStack item, boolean silent) {

    }

    @Override
    public ItemStack getItemInOffHand() {
        return null;
    }

    @Override
    public void setItemInOffHand(ItemStack item) {

    }

    @Override
    public void setItemInOffHand(ItemStack item, boolean silent) {

    }

    @Override
    public ItemStack getItemInHand() {
        return null;
    }

    @Override
    public void setItemInHand(ItemStack stack) {

    }

    @Override
    public int getHeldItemSlot() {
        return 0;
    }

    @Override
    public void setHeldItemSlot(int slot) {

    }
}
