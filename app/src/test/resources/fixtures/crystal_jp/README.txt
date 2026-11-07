Synthetic WRAM for Japanese Crystal. Not a ROM, not a save, and not a capture: every byte is zero
except the fields written below, so the decoder can be checked without a cart. Addresses are the
Glitch City Japanese Crystal GameShark list and the public symbol list (wPokemonData 01:DC9D,
wPlayerData 01:D48C). wram.bin is C000-DFFF, hram.bin is FF80-FFFE, wramx.bin is 8 banks of 0x1000
(bank 1 is where Crystal keeps this state).

Bank 1:
  0xD483 player gender 0
  0xD4B7 clock, 1 hour
  0xD841 money 300 (00 01 2C)
  0xD885 items: count 1, item 4, quantity 2, FF
  0xD8AF key items: count 0, FF
  0xD8CA balls: count 0, FF
  0xDC73 backup map 1, 10
  0xDC7B map group 1, number 10, y 4, x 6
  0xDC9D party count 1, species 25, FF
  0xDCA5 party mon 1: PIKACHU, level 12, HP 30
  0xDDE9 nickname ピカチュウ (41 85 90 AE 82 50)
  0xDE2D owned dex, species 1's bit
  0xD25E battle mode 1 (wild)
  0xD237 foe battle mon: species 16, level 3, HP 10

WRAM0:
  0xC622 player battle mon: species 25, level 12, HP 30
